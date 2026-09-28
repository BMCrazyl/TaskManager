/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.EmployeeDao;
import dao.ProjectDao;
import dao.TaskDao;
import model.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author mai09
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", ""})
public class DashboardServlet extends HttpServlet {
    private TaskDao taskDao = new TaskDao();
    private EmployeeDao employeeDao = new EmployeeDao();
    private ProjectDao projectDao = new ProjectDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1. Nhận tham số tìm kiếm từ giao diện (nếu có)
        String empName = request.getParameter("empName");
        String projectName = request.getParameter("projectName");
        String status = request.getParameter("status");

        // 2. Lấy danh sách công việc theo bộ lọc
        List<Task> taskList = taskDao.searchTasks(empName, projectName, status);

        // 3. Tính toán các số liệu thống kê
        int totalEmployees = employeeDao.getAll().size();
        int totalProjects = projectDao.getAll().size();
        int completedTasks = taskDao.countByStatus("Hoàn thành");
        int inProgressTasks = taskDao.countByStatus("Đang thực hiện");
        int overdueTasks = taskDao.countOverdue();

        // 4. Đẩy toàn bộ dữ liệu sang JSP với đúng tên biến hiển thị
        request.setAttribute("taskList", taskList);
        request.setAttribute("totalEmployees", totalEmployees);
        request.setAttribute("totalProjects", totalProjects);
        request.setAttribute("completedTasks", completedTasks);
        request.setAttribute("inProgressTasks", inProgressTasks);
        request.setAttribute("overdueTasks", overdueTasks);

        // Chuyển tiếp tới giao diện dashboard.jsp
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
