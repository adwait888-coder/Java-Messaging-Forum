import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ControlPanel")
public class ControlPanel extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [F1, F2, F3] 
        String action = request.getParameter("action");
        
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        StringBuilder resultData = new StringBuilder();

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatterbox_db", "root", "mysql");
            Statement stmt = con.createStatement();

            // २. [F1] TOGGLE BOT (MySQL Event on/off)
            if ("toggleBot".equals(action)) {
                
                ResultSet rs = stmt.executeQuery("SHOW VARIABLES LIKE 'event_scheduler'");
                if (rs.next()) {
                    String currentStatus = rs.getString("Value");
                    if ("ON".equalsIgnoreCase(currentStatus)) {
                        stmt.execute("SET GLOBAL event_scheduler='OFF'");
                        resultData.append("SYSTEM ALERT: AUTO-DELETE BOT HAS BEEN DISABLED.\n");
                    } else {
                        stmt.execute("SET GLOBAL event_scheduler='ON'");
                        resultData.append("SYSTEM ALERT: AUTO-DELETE BOT IS NOW ACTIVE.\n");
                    }
                }
            }

            // ३. [F2] FETCH DATA (backup_messages)
            else if ("fetchData".equals(action)) {
                ResultSet rs = stmt.executeQuery("SELECT sender, msg, msg_time,deleted_at FROM messages_backup ORDER BY msg_time DESC");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    resultData.append("[").append(rs.getString("msg_time")).append("] ")
				.append("[").append(rs.getString("deleted_at")).append("] ")
                              .append(rs.getString("sender")).append(": ")
                              .append(rs.getString("msg")).append("\n");
				
                }
                if (!found) {
                    resultData.append("--- NO DELETED LOGS FOUND IN DATABASE ---");
                }
            }

            // ४. [F3] MANUALLY PURGE 
            else if ("purgeData".equals(action)) {
                stmt.executeUpdate("TRUNCATE TABLE messages_backup");
                resultData.append("SYSTEM NOTIFICATION: BACKUP DATABASE PURGED SUCCESSFULLY.");
            }

            con.close();
        } catch (Exception e) {
            resultData.append("CRITICAL SYSTEM ERROR: ").append(e.getMessage());
        }

        
        out.print(resultData.toString());
        out.flush();
    }
}