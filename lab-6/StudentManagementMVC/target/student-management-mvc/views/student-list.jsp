<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 32px;
        }
        
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-style: italic;
        }
        
        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            font-weight: 500;
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
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-danger {
            background-color: #dc3545;
            color: white;
            padding: 8px 16px;
            font-size: 13px;
        }
        
        .btn-danger:hover {
            background-color: #c82333;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            font-weight: 600;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.5px;
        }
        
        tbody tr {
            transition: background-color 0.2s;
        }
        
        tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .actions {
            display: flex;
            gap: 10px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
        .pagination {
            margin: 20px 0;
            text-align: center;
        }

        .pagination a {
            padding: 8px 12px;
            margin: 0 4px;
            border: 1px solid #ddd;
            text-decoration: none;
        }

        .pagination strong {
            padding: 8px 12px;
            margin: 0 4px;
            background-color: #4CAF50;
            color: white;
            border: 1px solid #4CAF50;
        }
        .navbar {
            background: #2c3e50;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin: 0;
        }
        
        .navbar h2 {
            font-size: 20px;
        }
        
        .navbar-right {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .role-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .role-admin {
            background: #e74c3c;
        }
        
        .role-user {
            background: #3498db;
        }
        
        .btn-logout {
            padding: 8px 20px;
            background: #e74c3c;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 14px;
            transition: background 0.3s;
        }
        
        .btn-logout:hover {
            background: #c0392b;
        }
        .input-value{
            width: 14rem;
            border-radius: 0.375rem;
            padding-top: 0.375rem;
            padding-bottom: 0.375rem;
            padding-left: 0.5rem;
            padding-right: 0.5rem;
  /* Ring = box-shadow; using ring-inset + ring-1 + ring-gray-400 */
            box-shadow: inset 0 0 0 1px rgb(156, 163, 175);
  /* Optional: add border or outline for better effect */
            border: none;
            outline: none;
        }
        .input-value:focus{
            color: rgb(31, 41, 55);
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <div class="navbar">
        <h2>📚 Student Management System</h2>
        <div class="navbar-right">
            <div class="user-info">
                <span>Welcome, ${sessionScope.fullName}</span>
                <span class="role-badge role-${sessionScope.role}">
                    ${sessionScope.role}
                </span>
            </div>
            <a href="dashboard" class="btn btn-primary">Dashboard</a>
            <a href="logout" class="btn-logout">Logout</a>
        </div>
    </div>
                
    <div class="container">
        <h1>📚 Student Management System</h1>
        <p class="subtitle">MVC Pattern with Jakarta EE & JSTL</p>
        
        <!-- Success Message -->
        <c:if test="${not empty param.message}">
            <div class="message success">
                ✅ ${param.message}
            </div>
        </c:if>
        
        <!-- Error Message -->
        <c:if test="${not empty param.error}">
            <div class="message error">
                ❌ ${param.error}
            </div>
        </c:if>
        
        <!-- Add button - Admin only -->
        <c:if test="${sessionScope.role eq 'admin'}">
            <div style="margin: 20px 0;" class="btn btn-primary">
                <a href="student?action=new" class="btn-add">➕ Add New Student</a>
            </div>
        </c:if>
        
        <div class="search-box">
            <form action="student" method="get">
                <input type="hidden" name="action" value="search">
                <input type="text" name="keyword" value="${keyword}" placeholder="Enter keyword..." class="input-value">
        
                <button type="submit" class="btn btn-primary">Search</button>
                <c:if test="${not empty keyword}">
                    <a href="student?action=list" class="btn btn-danger">Clear</a>
                </c:if>
            </form>
        </div>

        <!-- Show message if available -->
        <c:if test="${not empty message}">
            <div class="message">${message}</div>
        </c:if>
        
        <div style="margin: 10px 0px;">
        <thead>
            <tr>
                <th><a class="btn btn-primary" href="student?action=list&sortBy=id&order=${sortBy == 'id' && order == 'asc' ? 'desc' : 'asc'}&major=${major != null ? major : ''}">ID <c:if test="${sortBy == 'id'}">
                    ${order == 'asc' ? '▲' : '▼'}
                </c:if></a></th>
                <th><a class="btn btn-primary" href="student?action=list&sortBy=student_code&order=${sortBy == 'student_code' && order == 'asc' ? 'desc' : 'asc'}&major=${major != null ? major : ''}">Code <c:if test="${sortBy == 'student_code'}">
                    ${order == 'asc' ? '▲' : '▼'}
                </c:if></a></th>
                <th><a class="btn btn-primary" href="student?action=list&sortBy=full_name&order=${sortBy == 'full_name' && order == 'asc' ? 'desc' : 'asc'}&major=${major != null ? major : ''}">Name <c:if test="${sortBy == 'full_name'}">
                    ${order == 'asc' ? '▲' : '▼'}
                </c:if></a></th>
                <th><a class="btn btn-primary" href="student?action=list&sortBy=major&order=${sortBy == 'major' && order == 'asc' ? 'desc' : 'asc'}&major=${major != null ? major : ''}">Major <c:if test="${sortBy == 'major'}">
                    ${order == 'asc' ? '▲' : '▼'}
                </c:if></a></th>
            </tr>
        </thead>
        </div>

        <div class="filter-box">
            <form action="student" method="get">
                <input type="hidden" name="action" value="list">
                <input type="hidden" name="sortBy" value="${sortBy}">
                <input type="hidden" name="order" value="${order}">
                <label>Filter by Major:</label>
                <select name="major" class="input-value">
                    <option value="">All Majors</option>
                    <option value="Computer Science" ${major == 'Computer Science' ? 'selected' : ''}>Computer Science</option>
                    <option value="Information Technology" ${major == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                    <option value="Software Engineering" ${major == 'Software Engineering' ? 'selected' : ''}>Software Engineering</option>
                    <option value="Business Administration" ${major == 'Business Administration' ? 'selected' : ''}>Business Administration</option>
                </select>
                <button type="submit" class="btn btn-primary">Apply Filter</button>
                <c:if test="${not empty major}">
                    <a href="student?action=list" class="btn btn-danger">Clear Filter</a>
                </c:if>
            </form>
        </div>

         <!-- Student Table -->
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Major</th>
                    <c:if test="${sessionScope.role eq 'admin'}">
                        <th>Actions</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="student" items="${students}">
                    <tr>
                        <td>${student.id}</td>
                        <td>${student.studentCode}</td>
                        <td>${student.fullName}</td>
                        <td>${student.email}</td>
                        <td>${student.major}</td>
                        
                        <!-- Action buttons - Admin only -->
                        <c:if test="${sessionScope.role eq 'admin'}">
                            <td>
                                <a href="student?action=edit&id=${student.id}" 
                                   class="btn btn-primary">Edit</a>
                                <a href="student?action=delete&id=${student.id}" 
                                   class="btn btn-danger"
                                   onclick="return confirm('Delete this student?')">Delete</a>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
                
                <c:if test="${empty students}">
                    <tr>
                        <td colspan="6" style="text-align: center;">
                            No students found
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
         
        <div class="pagination">
            <!-- Previous button -->
            <c:if test="${currentPage > 1}">
                <a href="student?action=list&page=${currentPage - 1}">« Previous</a>
            </c:if>
            
            <!-- Page numbers -->
            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <strong>${i}</strong>
                    </c:when>
                    <c:otherwise>
                        <a href="student?action=list&page=${i}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
    
            <!-- Next button -->
            <c:if test="${currentPage < totalPages}">
                <a href="student?action=list&page=${currentPage + 1}">Next »</a>
            </c:if>
        </div>

        <p>Showing page ${currentPage} of ${totalPages}</p>
    </div>
</body>
</html>
