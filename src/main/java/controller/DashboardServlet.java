/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.TaskDao;
import model.Account;
import model.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author mai09
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private TaskDao taskDao = new TaskDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Account currentUser = (session != null) ? (Account) session.getAttribute("user") : null;

        // Nếu chưa đăng nhập thì chuyển hướng về trang login
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String empName = request.getParameter("empName");
        String projectName = request.getParameter("projectName");
        String status = request.getParameter("status");

        // PHÂN QUYỀN HIỂN THỊ:
        // Nếu là EMPLOYEE, ép buộc empName chính là họ tên của nhân viên đang đăng nhập
        if ("EMPLOYEE".equalsIgnoreCase(currentUser.getRole())) {
            empName = currentUser.getFullname();
        }

        List<Task> taskList = taskDao.searchTasks(empName, projectName, status);

        request.setAttribute("taskList", taskList);
        request.setAttribute("overdueTasks", taskDao.countOverdue());
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}