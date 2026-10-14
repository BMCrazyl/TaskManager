package controller;

import dao.ProjectDao;
import dao.TaskDao;
import model.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "TaskServlet", urlPatterns = {"/tasks"})
public class TaskServlet extends HttpServlet {
    private final TaskDao taskDao = new TaskDao();
    private final ProjectDao projectDao = new ProjectDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        projectDao.ensureArchiveSchema();
        projectDao.archiveCompletedProjects();
        String action=request.getParameter("action"), idParam=request.getParameter("id");
        String keyword=request.getParameter("keyword");
        if ("edit".equals(action) && idParam != null) {
            try {
                Task task=taskDao.getById(Integer.parseInt(idParam));
                if (task != null && !"Hoàn thành".equals(task.getStatus()) && !"Hoan thanh".equals(task.getStatus()))
                    request.setAttribute("task",task);
            } catch (NumberFormatException e) { response.sendError(400,"Mã công việc không hợp lệ."); return; }
        }
        request.setAttribute("searchKeyword", keyword == null ? "" : keyword);
        request.setAttribute("projectList",projectDao.getAll());
        request.setAttribute("taskList",taskDao.getOpenTasks(keyword));
        request.getRequestDispatcher("/task-list.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String idStr=request.getParameter("id"), taskName=request.getParameter("taskName");
            int projectId=Integer.parseInt(request.getParameter("projectId"));
            Date deadline=Date.valueOf(request.getParameter("deadline"));
            String priority=request.getParameter("priority"), status=request.getParameter("status"),
                   description=request.getParameter("description");
            Task task=new Task();
            task.setTaskName(taskName); task.setProjectId(projectId); task.setDeadline(deadline);
            task.setPriority(priority); task.setStatus(status); task.setDescription(description);
            if (idStr != null && !idStr.trim().isEmpty()) {
                task.setId(Integer.parseInt(idStr)); taskDao.update(task);
            } else taskDao.insert(task);
            projectDao.archiveCompletedProjects();
        } catch (Exception e) { System.err.println("Lỗi lưu công việc: "+e.getMessage()); e.printStackTrace(); }
        response.sendRedirect(request.getContextPath()+"/tasks");
    }
}
