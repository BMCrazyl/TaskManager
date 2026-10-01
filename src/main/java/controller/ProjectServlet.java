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

@WebServlet(name = "ProjectServlet", urlPatterns = {"/projects"})
public class ProjectServlet extends HttpServlet {
    private ProjectDao projectDao = new ProjectDao();
    private TaskDao taskDao = new TaskDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        // 1. Xem công việc thuộc dự án
        if ("viewTasks".equals(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Project currentProj = projectDao.getById(id);
                List<Task> projectTasks = taskDao.getTasksByProjectId(id);
                
                request.setAttribute("selectedProject", currentProj);
                request.setAttribute("projectTasks", projectTasks);
                request.setAttribute("showTaskModal", true); // Bật cờ để tự động mở Popup
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 2. Chức năng Sửa
        else if ("edit".equals(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Project p = projectDao.getById(id);
                request.setAttribute("project", p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        // 3. Chức năng Xóa
        else if ("delete".equals(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                projectDao.delete(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/projects");
            return;
        }

        request.setAttribute("projectList", projectDao.getAll());
        request.getRequestDispatcher("/project-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String projectName = request.getParameter("projectName");
        Date startDate = Date.valueOf(request.getParameter("startDate"));
        Date endDate = Date.valueOf(request.getParameter("endDate"));
        String description = request.getParameter("description");

        Project p = new Project();
        p.setProjectName(projectName);
        p.setStartDate(startDate);
        p.setEndDate(endDate);
        p.setDescription(description);

        if (idStr != null && !idStr.trim().isEmpty()) {
            p.setId(Integer.parseInt(idStr));
            projectDao.update(p);
        } else {
            projectDao.insert(p);
        }

        response.sendRedirect(request.getContextPath() + "/projects");
    }
}