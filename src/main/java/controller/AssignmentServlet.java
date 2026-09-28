/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.AssignmentDao;
import dao.EmployeeDao;
import dao.TaskDao;
import model.Assignment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
/**
 *
 * @author mai09
 */
@WebServlet(name = "AssignmentServlet", urlPatterns = {"/assign"})
public class AssignmentServlet extends HttpServlet {
    private TaskDao taskDao = new TaskDao();
    private EmployeeDao employeeDao = new EmployeeDao();
    private AssignmentDao assignmentDao = new AssignmentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        request.setAttribute("taskList", taskDao.getAll());
        request.setAttribute("employeeList", employeeDao.getAll());
        request.getRequestDispatcher("/assign.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            String dateStr = request.getParameter("assignedDate");

            System.out.println(">>> ĐANG PHÂN CÔNG: Task ID = " + taskId + ", Employee ID = " + employeeId + ", Date = " + dateStr);

            Date assignedDate = (dateStr != null && !dateStr.trim().isEmpty())
                    ? Date.valueOf(dateStr)
                    : new Date(System.currentTimeMillis());

            Assignment a = new Assignment();
            a.setTaskId(taskId);
            a.setEmployeeId(employeeId);
            a.setAssignedDate(assignedDate);

            boolean result = assignmentDao.assignTask(a);
            System.out.println(">>> KẾT QUẢ LƯU CSDL: " + result);

        } catch (Exception e) {
            System.err.println(">>> LỖI PHÂN CÔNG: " + e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}