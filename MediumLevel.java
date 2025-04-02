CREATE DATABASE company;
USE company;

CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    department VARCHAR(50),
    salary DECIMAL(10,2)
);

INSERT INTO employees (name, department, salary) VALUES
('Purwa', 'HR', 50000),
('Prince', 'IT', 70000),
('Lakshya', 'Finance', 60000);

<!DOCTYPE html>
<html>
<head>
    <title>Employee Directory</title>
</head>
<body>
    <h2>Employee List</h2>
    <form action="EmployeeServlet" method="get">
        <input type="submit" value="View All Employees">
    </form>

    <h2>Search Employee by ID</h2>
    <form action="EmployeeServlet" method="get">
        <label>Enter Employee ID:</label>
        <input type="text" name="id" required>
        <input type="submit" value="Search">
    </form>
</body>
</html>

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

@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/company";
    private static final String JDBC_USER = "root";  // Change this to your DB username
    private static final String JDBC_PASSWORD = "password";  // Change this to your DB password

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Check if a specific employee ID is requested
            String empId = request.getParameter("id");

            if (empId != null) {
                // Fetch employee details by ID
                String sql = "SELECT * FROM employees WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(empId));
                ResultSet rs = stmt.executeQuery();

                out.println("<h2>Employee Details</h2>");
                if (rs.next()) {
                    out.println("ID: " + rs.getInt("id") + "<br>");
                    out.println("Name: " + rs.getString("name") + "<br>");
                    out.println("Department: " + rs.getString("department") + "<br>");
                    out.println("Salary: $" + rs.getDouble("salary") + "<br>");
                } else {
                    out.println("Employee not found.");
                }
            } else {
                // Fetch all employees
                String sql = "SELECT * FROM employees";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                out.println("<h2>Employee List</h2>");
                out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>Department</th><th>Salary</th></tr>");
                while (rs.next()) {
                    out.println("<tr><td>" + rs.getInt("id") + "</td><td>" +
                                rs.getString("name") + "</td><td>" +
                                rs.getString("department") + "</td><td>$" +
                                rs.getDouble("salary") + "</td></tr>");
                }
                out.println("</table>");
            }
            conn.close();
        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}

