import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/ChatServlet", asyncSupported = true)
public class ChatServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String messageContent = request.getParameter("message");
        
        
        
        String tabId = request.getParameter("tabId"); 
        
        HttpSession session = request.getSession(false);
        String currentSender = "Guest"; 

        if (session != null) {
           
            String tabSpecificUser = (String) session.getAttribute("user_" + tabId);
            if (tabSpecificUser != null) {
                currentSender = tabSpecificUser;
            } else if (session.getAttribute("user") != null) {
                currentSender = (String) session.getAttribute("user");
            }
        }
        
        
        final String finalSender = currentSender; 

        final AsyncContext asyncContext = request.startAsync();
        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatterbox_db", "root", "mysql");
                    
                    PreparedStatement ps = con.prepareStatement("INSERT INTO messages(sender, msg) VALUES(?, ?)");
                    ps.setString(1, finalSender); 
                    ps.setString(2, messageContent);
                    ps.executeUpdate();
                    
                    con.close();
                    asyncContext.complete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        
        response.sendRedirect("chatdome.html?tabId=" + (tabId != null ? tabId : ""));
    }
}