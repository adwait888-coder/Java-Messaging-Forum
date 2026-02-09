import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Form madhun data ghene
        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatterbox_db", "root", "mysql");
            
            // Query prepare karne
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(fullname, username, email, password) VALUES(?,?,?,?)");
            ps.setString(1, fullname);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            
            int status = ps.executeUpdate();
            if(status > 0) {
                // Success jhale ki login page (chatterbox.html) var pathva
                response.sendRedirect("chatterbox.html");
            }
            con.close();
        } catch(Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}