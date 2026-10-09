package dao;

import model.ProjectReport;
import util.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectReportDao {

    public boolean ensureSchema() {
        String sql = "IF OBJECT_ID(N'dbo.ProjectReport', N'U') IS NULL BEGIN " +
            "CREATE TABLE dbo.ProjectReport (" +
            "id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY, " +
            "project_id INT NOT NULL, task_id INT NOT NULL, employee_id INT NOT NULL, " +
            "original_file_name NVARCHAR(255) NOT NULL, content_type NVARCHAR(150) NOT NULL, " +
            "file_size BIGINT NOT NULL, file_data VARBINARY(MAX) NOT NULL, " +
            "submitted_at DATETIME2(0) NOT NULL CONSTRAINT DF_ProjectReport_submitted_at DEFAULT SYSDATETIME(), " +
            "review_status NVARCHAR(30) NOT NULL CONSTRAINT DF_ProjectReport_review_status DEFAULT N'Chờ tiếp nhận', " +
            "received_by NVARCHAR(100) NULL, received_at DATETIME2(0) NULL, " +
            "CONSTRAINT FK_ProjectReport_Project FOREIGN KEY (project_id) REFERENCES dbo.Project(id) ON DELETE CASCADE, " +
            "CONSTRAINT FK_ProjectReport_Task FOREIGN KEY (task_id) REFERENCES dbo.Task(id) ON DELETE CASCADE, " +
            "CONSTRAINT FK_ProjectReport_Employee FOREIGN KEY (employee_id) REFERENCES dbo.Employee(id) ON DELETE CASCADE" +
            "); END";
        try (Connection conn = DBConnect.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (Exception e) {
            System.err.println("Không thể khởi tạo bảng ProjectReport: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean uploadForAssignedTask(int taskId, String employeeName, String fileName,
                                         String contentType, byte[] data) {
        if (!ensureSchema() || data == null || data.length == 0) return false;
        String sql = "INSERT INTO dbo.ProjectReport " +
            "(project_id, task_id, employee_id, original_file_name, content_type, file_size, file_data, submitted_at, review_status) " +
            "SELECT p.id, t.id, e.id, ?, ?, ?, ?, SYSDATETIME(), N'Chờ tiếp nhận' " +
            "FROM dbo.Assignment a " +
            "JOIN dbo.Employee e ON e.id = a.employee_id " +
            "JOIN dbo.Task t ON t.id = a.task_id " +
            "JOIN dbo.Project p ON p.id = t.project_id " +
            "WHERE t.id = ? AND LTRIM(RTRIM(e.name)) = LTRIM(RTRIM(?))";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, fileName);
            ps.setNString(2, contentType == null ? "application/octet-stream" : contentType);
            ps.setLong(3, data.length);
            ps.setBytes(4, data);
            ps.setInt(5, taskId);
            ps.setNString(6, employeeName);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println("Lỗi lưu báo cáo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<ProjectReport> getAll(String keyword) {
        List<ProjectReport> list = new ArrayList<>();
        if (!ensureSchema()) return list;
        boolean filtered = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT r.id, r.project_id, r.task_id, r.employee_id, r.original_file_name, " +
            "r.content_type, r.file_size, r.submitted_at, r.review_status, r.received_by, r.received_at, " +
            "p.projectName, t.taskName, e.name AS employeeName " +
            "FROM dbo.ProjectReport r JOIN dbo.Project p ON p.id=r.project_id " +
            "JOIN dbo.Task t ON t.id=r.task_id JOIN dbo.Employee e ON e.id=r.employee_id " +
            (filtered ? "WHERE p.projectName LIKE ? OR t.taskName LIKE ? OR e.name LIKE ? OR r.original_file_name LIKE ? " : "") +
            "ORDER BY CASE WHEN r.review_status=N'Chờ tiếp nhận' THEN 0 ELSE 1 END, r.submitted_at DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (filtered) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setNString(1, pattern); ps.setNString(2, pattern);
                ps.setNString(3, pattern); ps.setNString(4, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(readReport(rs, false));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<ProjectReport> getForProject(int projectId) {
        List<ProjectReport> list = new ArrayList<>();
        if (!ensureSchema()) return list;
        String sql = "SELECT r.id, r.project_id, r.task_id, r.employee_id, r.original_file_name, " +
            "r.content_type, r.file_size, r.submitted_at, r.review_status, r.received_by, r.received_at, " +
            "p.projectName, t.taskName, e.name AS employeeName " +
            "FROM dbo.ProjectReport r JOIN dbo.Project p ON p.id=r.project_id " +
            "JOIN dbo.Task t ON t.id=r.task_id JOIN dbo.Employee e ON e.id=r.employee_id " +
            "WHERE r.project_id=? ORDER BY r.submitted_at DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(readReport(rs, false)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ProjectReport getById(long id) {
        if (!ensureSchema()) return null;
        String sql = "SELECT r.*, p.projectName, t.taskName, e.name AS employeeName " +
            "FROM dbo.ProjectReport r JOIN dbo.Project p ON p.id=r.project_id " +
            "JOIN dbo.Task t ON t.id=r.task_id JOIN dbo.Employee e ON e.id=r.employee_id WHERE r.id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return readReport(rs, true);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean markReceived(long id, String adminName) {
        if (!ensureSchema()) return false;
        String sql = "UPDATE dbo.ProjectReport SET review_status=N'Đã tiếp nhận', received_by=?, " +
                     "received_at=SYSDATETIME() WHERE id=? AND review_status=N'Chờ tiếp nhận'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, adminName);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private ProjectReport readReport(ResultSet rs, boolean includeContent) throws SQLException {
        ProjectReport r = new ProjectReport();
        r.setId(rs.getLong("id"));
        r.setProjectId(rs.getInt("project_id"));
        r.setTaskId(rs.getInt("task_id"));
        r.setEmployeeId(rs.getInt("employee_id"));
        r.setOriginalFileName(rs.getNString("original_file_name"));
        r.setContentType(rs.getNString("content_type"));
        r.setFileSize(rs.getLong("file_size"));
        r.setSubmittedAt(rs.getTimestamp("submitted_at"));
        r.setReviewStatus(rs.getNString("review_status"));
        r.setReceivedBy(rs.getNString("received_by"));
        r.setReceivedAt(rs.getTimestamp("received_at"));
        r.setProjectName(rs.getNString("projectName"));
        r.setTaskName(rs.getNString("taskName"));
        r.setEmployeeName(rs.getNString("employeeName"));
        if (includeContent) r.setFileData(rs.getBytes("file_data"));
        return r;
    }
}
