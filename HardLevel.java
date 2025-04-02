CREATE DATABASE student_portal;
USE student_portal;

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    date DATE NOT NULL,
    status ENUM('Present', 'Absent') NOT NULL
);

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Attendance Portal</title>
</head>
<body>
    <h2>Enter Attendance Details</h2>
    <form action="AttendanceServlet" method="post">
        <label>Student Name:</label>
        <input type="text" name="student_name" required><br><br>

        <label>Roll Number:</label>
        <input type="text" name="roll_number" required><br><br>

        <label>Date:</label>
        <input type="date" name="date" required><br><br>

        <label>Status:</label>
        <select name="status">
            <option value="Present">Present</option>
            <option value="Absent">Absent</option>
        </select><br><br>

        <input type="submit" value="Submit Attendance">
    </form>

    <h2>View Attendance Records</h2>
    <form action="ViewAttendanceServlet" method="get">
        <input type="submit" value="View Records">
    </form>
</body>
</html>

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/student_portal";
    private static final String JDBC_USER = "root";  // Change to your MySQL username
    private static final String JDBC_PASSWORD = "password";  // Change to your MySQL password

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("student_name");
        String rollNumber = request.getParameter("roll_number");
        String date = request.getParameter("date");
        String status = request.getParameter("status");

        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Insert Data
            String sql = "INSERT INTO attendance (student_name, roll_number, date, status) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, rollNumber);
            stmt.setString(3, date);
            stmt.setString(4, status);

            int rowsInserted = stmt.executeUpdate();
            conn.close();

            if (rowsInserted > 0) {
                out.println("<h3>Attendance recorded successfully!</h3>");
            } else {
                out.println("<h3>Error saving attendance.</h3>");
            }
        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewAttendanceServlet")
public class ViewAttendanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/student_portal";
    private static final String JDBC_USER = "root";  
    private static final String JDBC_PASSWORD = "password";  

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Fetch Data
            String sql = "SELECT * FROM attendance";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            out.println("<h2>Attendance Records</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>Roll Number</th><th>Date</th><th>Status</th></tr>");

            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" +
                            rs.getString("student_name") + "</td><td>" +
                            rs.getString("roll_number") + "</td><td>" +
                            rs.getString("date") + "</td><td>" +
                            rs.getString("status") + "</td></tr>");
            }
            out.println("</table>");
            conn.close();
        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
