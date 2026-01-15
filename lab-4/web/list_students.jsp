<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.regex.Pattern" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h1 { color: #333; }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin-bottom: 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
        }
        th {
            background-color: #007bff;
            color: white;
            padding: 12px;
            text-align: left;
        }
        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        tr:hover { background-color: #f8f9fa; }
        .action-link {
            color: #007bff;
            text-decoration: none;
            margin-right: 10px;
        }
        .delete-link { color: #dc3545; }
        input[type="text"] {
            width: 50%; padding: 10px; border: 1px solid #ddd;
            border-radius: 5px; box-sizing: border-box;
        }
        
        .table-responsive {
            overflow-x: auto;
        }

        @media (max-width: 768px) {
            table {
                font-size: 12px;
            }
            th, td {
                padding: 5px;
            }
        }

    </style>
</head>
<body>
    <h1>📚 Student Management System</h1>
    
    <% if (request.getParameter("message") != null) { %>
        <div class="message success">
            ✓   <%= request.getParameter("message") %>
        </div>
    <% } %>
    
    <% if (request.getParameter("error") != null) { %>
        <div class="message error">
            ✗   <%= request.getParameter("error") %>
        </div>
    <% } %>
    
    <script>
    setTimeout(function() {
        var messages = document.querySelectorAll('.message');
        messages.forEach(function(msg) {
            msg.style.display = 'none';
        });
    }, 3000);
    </script>

    <a href="add_student.jsp" class="btn">➕ Add New Student</a>
    
    <form action="list_students.jsp" method="GET">
        <input type="text" name="keyword" placeholder="Search by name or code...">
        <button type="submit" class="btn">Search</button>
        <a href="list_students.jsp" class="btn">Clear</a>
    </form>
    
    <form method="get" action="list_students.jsp">
        <label for="sort">Sort by:</label>
        <select name="sort" id="sort">
            <option value="id">ID</option>
            <option value="student_code">Student Code</option>
            <option value="full_name">Full Name</option>
            <option value="email">Email</option>
            <option value="major">Major</option>
        </select>

        <label for="order">Order:</label>
        <select name="order" id="order">
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
        </select>

        <button type="submit" class="btn">Sort</button>
    </form>
    
    <a href="export_csv.jsp" class="btn">Export to CSV</a>
    
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Student Code</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Major</th>
                <th>Created At</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
<%
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    // Get page number from URL (default = 1)
    String pageParam = request.getParameter("page");
    int currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 1;

    // Records per page
    int recordsPerPage = 10;

    // Calculate offset
    int offset = (currentPage - 1) * recordsPerPage;

    int totalRecords;
    int totalPages = 1;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_management",
            "root",
            "123myIUcode321"
        );
        
        String sql;
        String keyword = request.getParameter("keyword");
        
        int count = 0;
        if (keyword != null && !keyword.isEmpty()) {
            sql = "SELECT COUNT(*) FROM students WHERE full_name LIKE ? OR student_code LIKE ? OR major LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
        } else {
            sql = "SELECT COUNT(*) FROM students";
            pstmt = conn.prepareStatement(sql);
        }
        rs = pstmt.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        totalRecords = count;
        totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        
        String sortBy = request.getParameter("sort"); // column name
        String order = request.getParameter("order"); // asc or desc
        if (sortBy == null) sortBy = "id";
        if (order == null) order = "desc";
        
        if (keyword != null && !keyword.isEmpty()) {
            // Search query with LIKE operator
            sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_code LIKE ? OR major LIKE ? ORDER BY " + sortBy + " " + order + " LIMIT ? OFFSET ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            pstmt.setInt(4, recordsPerPage);
            pstmt.setInt(5, offset);
            rs = pstmt.executeQuery();
        } else {
            // Normal query
            sql = "SELECT * FROM students ORDER BY " + sortBy + " " + order + " LIMIT ? OFFSET ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, recordsPerPage);
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
        }
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String studentCode = rs.getString("student_code");
            String fullName = rs.getString("full_name");
            String email = rs.getString("email");
            String major = rs.getString("major");
            Timestamp createdAt = rs.getTimestamp("created_at");
%>
            <tr>
                <td><%= id %></td>
                <td><% if (keyword != null && !keyword.isEmpty()) { %>
                    <%= studentCode.replaceAll("(?i)(" + Pattern.quote(keyword) + ")", "<span style='background:yellow'>$1</span>") %>
                    <% } else { %>
                    <%= studentCode %>
                    <% } %></td>
                <td><% if (keyword != null && !keyword.isEmpty()) { %>
                    <%= fullName.replaceAll("(?i)(" + Pattern.quote(keyword) + ")", "<span style='background:yellow'>$1</span>") %>
                    <% } else { %>
                    <%= fullName %>
                    <% } %></td>
                <td><%= email != null ? email : "N/A" %></td>
                <td><% if (major != null && keyword != null && !keyword.isEmpty()) { %>
                    <%= major.replaceAll("(?i)(" + Pattern.quote(keyword) + ")", "<span style='background:yellow'>$1</span>") %>
                    <% } else { %>
                    <%= major != null ? major : "N/A" %>
                    <% } %></td>
                <td><%= createdAt %></td>
                <td>
                    <a href="edit_student.jsp?id=<%= id %>" class="action-link">✏️ Edit</a>
                    <a href="delete_student.jsp?id=<%= id %>" 
                       class="action-link delete-link"
                       onclick="return confirm('Are you sure?')">🗑️ Delete</a>
                </td>
            </tr>
<%
        }
    } catch (ClassNotFoundException e) {
        out.println("<tr><td colspan='7'>Error: JDBC Driver not found!</td></tr>");
        e.printStackTrace();
    } catch (SQLException e) {
        out.println("<tr><td colspan='7'>Database Error: " + e.getMessage() + "</td></tr>");
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>
        </tbody>
    </table>
    <div class="pagination">
    <% if (currentPage > 1) { %>
        <a href="list_students.jsp?page=<%= currentPage - 1 %>" class="btn">Previous</a>
    <% } %>
    
    <% for (int i = 1; i <= totalPages; i++) { %>
        <% if (i == currentPage) { %>
            <strong class="btn"><%= i %></strong>
        <% } else { %>
            <a href="list_students.jsp?page=<%= i %>" class="btn"><%= i %></a>
        <% } %>
    <% } %>
    
    <% if (currentPage < totalPages) { %>
        <a href="list_students.jsp?page=<%= currentPage + 1 %>" class="btn">Next</a>
    <% } %>
    </div>
</body>
</html>
