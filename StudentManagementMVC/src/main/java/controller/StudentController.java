package controller;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":
                searchStudents(request, response);
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }
    
    // List all students
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String major = request.getParameter("major");
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");
        
        // Get current page (default to 1)
        String pageParam = request.getParameter("page");
        int currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
        
        // Records per page
        int recordsPerPage = 10;

        // Calculate offset
        int offset = (currentPage - 1) * recordsPerPage;
        
        // Get data
        List<Student> students = studentDAO.getStudentsPaginatedFiltered(major, sortBy, order, offset, recordsPerPage);
        int totalRecords = studentDAO.getTotalStudentsFiltered(major);

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        
        request.setAttribute("students", students);
        request.setAttribute("major", major);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for new student
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for editing student
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        request.setAttribute("student", existingStudent);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Insert new student
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student newStudent = new Student(studentCode, fullName, email, major);
        
        // 2. Validate
        if (!validateStudent(newStudent, request)) {
            // Set student as attribute (to preserve entered data)
            request.setAttribute("student", null);
            // Forward back to form
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return; // STOP here
        }
        
        if (studentDAO.addStudent(newStudent)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to add student");
        }
    }
    
    // Update student
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);
        student.setId(id);
        
        // 2. Validate
        if (!validateStudent(student, request)) {
            // Set student as attribute (to preserve entered data)
            request.setAttribute("student", student);
            // Forward back to form
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return; // STOP here
        }
        
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to update student");
        }
    }
    
    // Delete student
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    
    private void searchStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Get keyword parameter
        String keyword = request.getParameter("keyword");
    
        // 2. Decide which DAO method to call
        List<Student> students;
        if (keyword == null || keyword.trim().isEmpty()) {
        // 3. Get the student list
            students = studentDAO.getAllStudents();
            keyword = "";
        } else {
            students = studentDAO.searchStudents(keyword);
            if (students == null || students.isEmpty()) {
                request.setAttribute("message", "No students found");
            } else {
                request.setAttribute("message", "Search successful");
            }
        }
    
        // 4. Set request attributes
        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword);
    
        // 5. Forward to view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;

        // Validate student code
        String code = student.getStudentCode();
        String codePattern = "[A-Z]{2}[0-9]{3,}";
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else if (!code.matches(codePattern)) {
            request.setAttribute("errorCode", "Invalid format. Use 2 letters + 3+ digits (e.g., SV001)");
            isValid = false;
        }

        // Validate full name
        String name = student.getFullName();
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (name.trim().length() < 2) {
            request.setAttribute("errorName", "Full name must be at least 2 characters");
            isValid = false;
        }

        // Validate email (only if provided)
        String email = student.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }
    
        // Validate major
        String major = student.getMajor();
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        }
    
        return isValid;
    }
}
