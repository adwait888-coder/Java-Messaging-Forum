

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/GetMessagesServlet")
public class GetMessagesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. RESPONSE ENCODING SET KARNE (Emojis disnyasathi VVIP)
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 2. JDBC DRIVER LOAD KARA
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 3. CONNECTION URL (chatterbox_db ani utf8mb4 support sathi)
            String url = "jdbc:mysql://localhost:3306/chatterbox_db?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_general_ci";
            con = DriverManager.getConnection(url, "root", "mysql");
            
            stmt = con.createStatement();
            
            // 4. QUERY RUN KARA
            // 'msg_time' nusar sort kela aahe (Ascending order)
            rs = stmt.executeQuery("SELECT sender, msg FROM messages ORDER BY msg_time ASC");

            // 5. DATA FETCH KARUN HTML MADHE PRINT KARA
            while (rs.next()) {
                String sender = rs.getString("sender");
                String message = rs.getString("msg");
                
                // Style thodi sudharli aahe jeva fetch hoto
                out.println("<div style='margin-bottom:10px; padding:8px; border-bottom:1px solid #eee;'>");
                out.println("<strong>" + sender + ":</strong> " + message);
                out.println("</div>");
            }

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error fetching messages: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } finally {
            // 6. RESOURCES CLOSE KARA
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { }
            try { if (con != null) con.close(); } catch (SQLException e) { }
        }
    }
}