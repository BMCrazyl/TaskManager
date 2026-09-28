/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.ProjectDao;
import dao.TaskDao;
import model.Project;
import model.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
/**
 *
 * @author mai09
 */
@WebServlet(name = "TaskServlet", urlPatterns = {"/tasks"})
public class TaskServlet extends HttpServlet {
    private TaskDao taskDao = new TaskDao();
    private ProjectDao projectDao = new ProjectDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        // Khi người dùng bấm nút Sửa trên danh sách
        if ("edit".equals(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Task task = taskDao.getById(id);
                request.setAttribute("task", task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Nạp danh sách dự án cho dropdown và danh sách task cho bảng bên phải
        request.setAttribute("projectList", projectDao.getAll());
        request.setAttribute("taskList", taskDao.getAll());
        request.getRequestDispatcher("/task-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String taskName = request.getParameter("taskName");
        int projectId = Integer.parseInt(request.getParameter("projectId"));
        Date deadline = Date.valueOf(request.getParameter("deadline"));
        String priority = request.getParameter("priority");
        String status = request.getParameter("status");
        String description = request.getParameter("description");

        Task task = new Task();
        task.setTaskName(taskName);
        task.setProjectId(projectId);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setStatus(status);
        task.setDescription(description);

        if (idStr != null && !idStr.trim().isEmpty()) {
            // Cập nhật (UPDATE)
            task.setId(Integer.parseInt(idStr));
            taskDao.update(task);
        } else {
            // Thêm mới (INSERT)
            taskDao.insert(task);
        }
        response.sendRedirect(request.getContextPath() + "/tasks");
    }
}