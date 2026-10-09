package controller;

import dao.ProjectReportDao;
import model.Account;
import model.ProjectReport;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ProjectReportServlet", urlPatterns = {"/reports"})
public class ProjectReportServlet extends HttpServlet {
    private final ProjectReportDao reportDao = new ProjectReportDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account user = currentUser(request, response);
        if (user == null) return;
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ quản trị viên được tiếp nhận báo cáo.");
            return;
        }
        reportDao.ensureSchema();
        String action = request.getParameter("action");
        if ("download".equals(action)) {
            try {
                long id = Long.parseLong(request.getParameter("id"));
                ProjectReport report = reportDao.getById(id);
                if (report == null) { response.sendError(404, "Không tìm thấy báo cáo."); return; }
                String safeName = report.getOriginalFileName().replace("\r", "").replace("\n", "").replace("\"", "");
                response.setContentType("application/octet-stream");
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                        URLEncoder.encode(safeName, StandardCharsets.UTF_8).replace("+", "%20"));
                response.setContentLengthLong(report.getFileData().length);
                response.getOutputStream().write(report.getFileData());
            } catch (NumberFormatException e) { response.sendError(400, "Mã báo cáo không hợp lệ."); }
            return;
        }
        String keyword = request.getParameter("keyword");
        request.setAttribute("searchKeyword", keyword == null ? "" : keyword);
        request.setAttribute("reportList", reportDao.getAll(keyword));
        request.getRequestDispatcher("/report-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account user = currentUser(request, response);
        if (user == null) return;
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ quản trị viên được tiếp nhận báo cáo.");
            return;
        }
        try {
            long id = Long.parseLong(request.getParameter("id"));
            reportDao.markReceived(id, user.getFullname());
        } catch (Exception e) {
            request.getSession().setAttribute("reportMessage", "Không thể cập nhật trạng thái báo cáo.");
        }
        response.sendRedirect(request.getContextPath() + "/reports");
    }

    private Account currentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Account user = session == null ? null : (Account) session.getAttribute("user");
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return null; }
        return user;
    }
}
