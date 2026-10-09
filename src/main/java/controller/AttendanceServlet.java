package controller;

import dao.TaskDao;
import dao.ProjectDao;
import model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AttendanceServlet", urlPatterns = {"/attendance"})
public class AttendanceServlet extends HttpServlet {
    private final TaskDao taskDao = new TaskDao();
    private final ProjectDao projectDao = new ProjectDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account user = getCurrentUser(request, response);
        if (user == null) return;
        if (!"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ nhân viên mới được sử dụng chức năng này.");
            return;
        }

        List<Map<String, Object>> taskList = taskDao.getTasksForEmployee(user.getFullname());
        request.setAttribute("taskList", taskList);
        request.getRequestDispatcher("/attendance.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Account user = getCurrentUser(request, response);
        if (user == null) return;
        if (!"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ nhân viên mới được sử dụng chức năng này.");
            return;
        }

        try {
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            boolean success = taskDao.completeTaskFromAttendance(taskId, user.getFullname());
            if (success) projectDao.archiveCompletedProjects();
            HttpSession session = request.getSession();
            session.setAttribute("attendanceMessage", success
                    ? "Đã chấm công hoàn thành. Bảng quản lý công việc đã được cập nhật."
                    : "Không thể chấm công: công việc không được giao cho bạn hoặc đã hoàn thành.");
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("attendanceMessage", "Mã công việc không hợp lệ.");
        }
        response.sendRedirect(request.getContextPath() + "/attendance");
    }

    private Account getCurrentUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Account user = session == null ? null : (Account) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return user;
    }
}
