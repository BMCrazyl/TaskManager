package dao;

import model.Project;
import util.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao {
    public boolean ensureArchiveSchema() {
        String sql =
            "IF COL_LENGTH(N'dbo.Project', N'isArchived') IS NULL " +
            "BEGIN ALTER TABLE dbo.Project ADD isArchived BIT NOT NULL " +
            "CONSTRAINT DF_Project_isArchived DEFAULT (0) WITH VALUES; END; " +
            "IF COL_LENGTH(N'dbo.Project', N'archivedAt') IS NULL " +
            "BEGIN ALTER TABLE dbo.Project ADD archivedAt DATETIME2(0) NULL; END;";
        try (Connection conn = DBConnect.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (Exception e) {
            System.err.println("Không thể cập nhật schema lưu trữ dự án: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Project> getAll() { return getAll(null); }

    public List<Project> getAll(String keyword) {
        List<Project> list = new ArrayList<>();
        if (!ensureArchiveSchema()) return list;
        boolean filtered = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT * FROM Project WHERE ISNULL(isArchived, 0) = 0 " +
            (filtered ? "AND (projectName LIKE ? OR description LIKE ? OR CONVERT(NVARCHAR(30), startDate, 23) LIKE ? OR CONVERT(NVARCHAR(30), endDate, 23) LIKE ?) " : "") +
            "ORDER BY id DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (filtered) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setNString(1, pattern); ps.setNString(2, pattern);
                ps.setString(3, pattern); ps.setString(4, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(readProject(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Project> getArchived() { return getArchived(null); }

    public List<Project> getArchived(String keyword) {
        List<Project> list = new ArrayList<>();
        if (!ensureArchiveSchema()) return list;
        boolean filtered = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT * FROM Project WHERE isArchived = 1 " +
            (filtered ? "AND (projectName LIKE ? OR description LIKE ?) " : "") +
            "ORDER BY archivedAt DESC, id DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (filtered) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setNString(1, pattern); ps.setNString(2, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(readProject(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int archiveCompletedProjects() {
        if (!ensureArchiveSchema()) return 0;
        String sql =
            "UPDATE p SET isArchived = 1, archivedAt = COALESCE(p.archivedAt, SYSDATETIME()) " +
            "FROM dbo.Project p WHERE ISNULL(p.isArchived, 0) = 0 " +
            "AND EXISTS (SELECT 1 FROM dbo.Task t WHERE t.project_id = p.id) " +
            "AND NOT EXISTS (SELECT 1 FROM dbo.Task t WHERE t.project_id = p.id " +
            " AND LTRIM(RTRIM(ISNULL(t.status, N''))) NOT IN (N'Hoàn thành', N'Hoan thanh'))";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi tự lưu trữ dự án đã hoàn thành: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public boolean insert(Project proj) {
        String sql = "INSERT INTO Project (projectName, startDate, endDate, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, proj.getProjectName());
            ps.setDate(2, proj.getStartDate());
            ps.setDate(3, proj.getEndDate());
            ps.setNString(4, proj.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        if (!ensureArchiveSchema()) return false;
        String sql = "DELETE FROM Project WHERE id = ? AND ISNULL(isArchived, 0) = 0";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteArchived(int id) {
        if (!ensureArchiveSchema()) return false;
        String sql = "DELETE FROM Project WHERE id = ? AND isArchived = 1";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi xóa dự án khỏi lịch sử: " + e.getMessage());
            e.printStackTrace(); return false;
        }
    }

    public Project getById(int id) {
        if (!ensureArchiveSchema()) return null;
        String sql = "SELECT * FROM Project WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return readProject(rs); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean update(Project p) {
        if (!ensureArchiveSchema()) return false;
        String sql = "UPDATE Project SET projectName = ?, startDate = ?, endDate = ?, description = ? " +
                     "WHERE id = ? AND ISNULL(isArchived, 0) = 0";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, p.getProjectName());
            ps.setDate(2, p.getStartDate());
            ps.setDate(3, p.getEndDate());
            ps.setNString(4, p.getDescription());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private Project readProject(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setId(rs.getInt("id"));
        p.setProjectName(rs.getNString("projectName"));
        p.setStartDate(rs.getDate("startDate"));
        p.setEndDate(rs.getDate("endDate"));
        p.setDescription(rs.getNString("description"));
        p.setArchived(rs.getBoolean("isArchived"));
        p.setArchivedAt(rs.getTimestamp("archivedAt"));
        return p;
    }
}
