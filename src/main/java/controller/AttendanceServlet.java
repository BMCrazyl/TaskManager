package controller;

import dao.TaskDao;
import dao.ProjectDao;
import dao.ProjectReportDao;
import model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Locale;

@WebServlet(name = "AttendanceServlet", urlPatterns = {"/attendance"})
@MultipartConfig(maxFileSize = 10485760, maxRequestSize = 11534336, fileSizeThreshold = 1048576)
public class AttendanceServlet extends HttpServlet {
    private static final long MAX_REPORT_BYTES = 10L * 1024L * 1024L;
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
        "pdf", "doc", "docx", "xls", "xlsx", "csv", "txt", "png", "jpg", "jpeg", "zip"
    );
    private final TaskDao taskDao = new TaskDao();
    private final ProjectDao projectDao = new ProjectDao();
    private final ProjectReportDao reportDao = new ProjectReportDao();

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

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        if ("uploadReport".equals(action)) {
            uploadReport(request, response, user, session);
            response.sendRedirect(request.getContextPath() + "/attendance");
            return;
        }

        try {
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            boolean success = taskDao.completeTaskFromAttendance(taskId, user.getFullname());
            if (success) projectDao.archiveCompletedProjects();
            session.setAttribute("attendanceMessage", success
                    ? "Đã chấm công hoàn thành. Bảng quản lý công việc đã được cập nhật."
                    : "Không thể chấm công: công việc không được giao cho bạn hoặc đã hoàn thành.");
        } catch (NumberFormatException e) {
            session.setAttribute("attendanceMessage", "Mã công việc không hợp lệ.");
        }
        response.sendRedirect(request.getContextPath() + "/attendance");
    }

    private void uploadReport(HttpServletRequest request, HttpServletResponse response, Account user,
                              HttpSession session) throws IOException, ServletException {
        try {
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            Part filePart = request.getPart("reportFile");
            if (filePart == null || filePart.getSize() <= 0) {
                session.setAttribute("attendanceMessage", "Bạn cần chọn tệp báo cáo trước khi gửi.");
                return;
            }
            if (filePart.getSize() > MAX_REPORT_BYTES) {
                session.setAttribute("attendanceMessage", "Tệp vượt quá giới hạn 10 MB.");
                return;
            }
            String submittedName = filePart.getSubmittedFileName();
            if (submittedName == null || submittedName.trim().isEmpty()) {
                session.setAttribute("attendanceMessage", "Tên tệp không hợp lệ.");
                return;
            }
            submittedName = submittedName.replace("\\", "/");
            submittedName = submittedName.substring(submittedName.lastIndexOf('/') + 1).trim();
            if (submittedName.length() > 240 || submittedName.contains("..")) {
                session.setAttribute("attendanceMessage", "Tên tệp không hợp lệ.");
                return;
            }
            int dot = submittedName.lastIndexOf('.');
            String ext = dot < 0 ? "" : submittedName.substring(dot + 1).toLowerCase(Locale.ROOT);
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                session.setAttribute("attendanceMessage", "Định dạng chưa hỗ trợ. Cho phép PDF, Office, CSV, TXT, ảnh và ZIP.");
                return;
            }
            byte[] data;
            try (InputStream in = filePart.getInputStream()) { data = in.readNBytes((int) MAX_REPORT_BYTES + 1); }
            if (data.length == 0 || data.length > MAX_REPORT_BYTES) {
                session.setAttribute("attendanceMessage", "Tệp trống hoặc vượt quá giới hạn 10 MB.");
                return;
            }
            boolean saved = reportDao.uploadForAssignedTask(taskId, user.getFullname(), submittedName,
                    filePart.getContentType(), data);
            session.setAttribute("attendanceMessage", saved
                    ? "Đã gửi báo cáo cho quản trị viên. Báo cáo được lưu cùng dự án và công việc."
                    : "Không gửi được báo cáo: hãy kiểm tra phân công của bạn và cấu hình cơ sở dữ liệu.");
        } catch (NumberFormatException e) {
            session.setAttribute("attendanceMessage", "Mã công việc không hợp lệ.");
        } catch (IllegalStateException e) {
            session.setAttribute("attendanceMessage", "Tệp vượt quá giới hạn tải lên 10 MB.");
        }
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
