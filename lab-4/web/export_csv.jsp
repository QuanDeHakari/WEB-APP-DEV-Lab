<%@ page import="java.sql.*" %>
<%
response.setContentType("text/csv");
response.setHeader("Content-Disposition", "attachment; filename=\"students.csv\"");

Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_management",
            "root",
            "123myIUcode321"
);
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT id, student_code, full_name, email, major FROM students");

out.println("ID,Student Code,Full Name,Email,Major");

while (rs.next()) {
    out.println(
        rs.getInt("id") + "," +
        rs.getString("student_code") + "," +
        rs.getString("full_name") + "," +
        rs.getString("email") + "," +
        rs.getString("major")
    );
}

rs.close();
stmt.close();
conn.close();
%>