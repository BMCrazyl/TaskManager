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
    private final ProjectDao projectDao = new ProjectDao();
    private final TaskDao taskDao = new TaskDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        projectDao.ensureArchiveSchema();
        projectDao.archiveCompletedProjects();

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if ("viewTasks".equals(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                request.setAttribute("selectedProject", projectDao.getById(id));
                List<Task> projectTasks = taskDao.getTasksByProjectId(id);
                request.setAttribute("projectTasks", projectTasks);
                request.setAttribute("showTaskModal", true);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã dự án không hợp lệ.");
                return;
            }
        } else if ("edit".equals(action) && idParam != null) {
            try {
                Project p = projectDao.getById(Integer.parseInt(idParam));
                if (p != null && !p.isArchived()) request.setAttribute("project", p);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã dự án không hợp lệ.");
                return;
            }
        } else if ("delete".equals(action) && idParam != null) {
            try { projectDao.delete(Integer.parseInt(idParam)); }
            catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã dự án không hợp lệ."); return;
            }
            response.sendRedirect(request.getContextPath() + "/projects");
            return;
        } else if ("deleteHistory".equals(action) && idParam != null) {
            try { projectDao.deleteArchived(Integer.parseInt(idParam)); }
            catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã dự án không hợp lệ."); return;
            }
            response.sendRedirect(request.getContextPath() + "/projects");
            return;
        }

        request.setAttribute("projectList", projectDao.getAll());
        request.setAttribute("projectHistory", projectDao.getArchived());
        request.getRequestDispatcher("/project-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        projectDao.ensureArchiveSchema();

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
        } else projectDao.insert(p);

        projectDao.archiveCompletedProjects();
        response.sendRedirect(request.getContextPath() + "/projects");
    }
}
