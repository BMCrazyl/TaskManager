/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.ProjectDao;
import model.Project;
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
@WebServlet(name = "ProjectServlet", urlPatterns = {"/projects"})
public class ProjectServlet extends HttpServlet {
    private ProjectDao projectDao = new ProjectDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        // 1. Xử lý khi bấm nút Sửa
        if ("edit".equals(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Project project = projectDao.getById(id);
                request.setAttribute("project", project);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        // 2. Xử lý khi bấm nút Xóa
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

        // Tải danh sách dự án lên bảng
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
            // Trường hợp Cập nhật (UPDATE)
            p.setId(Integer.parseInt(idStr));
            projectDao.update(p);
        } else {
            // Trường hợp Thêm mới (INSERT)
            projectDao.insert(p);
        }

        response.sendRedirect(request.getContextPath() + "/projects");
    }
}