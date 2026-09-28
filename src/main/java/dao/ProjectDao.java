/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.Project;
import util.DBConnect;
import java.sql.*;
import java.util.*;
/**
 *
 * @author mai09
 */
public class ProjectDao {
   // 1. Lấy toàn bộ danh sách dự án
    public List<Project> getAll() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM Project ORDER BY id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Project(
                    rs.getInt("id"),
                    rs.getString("projectName"),
                    rs.getDate("startDate"),
                    rs.getDate("endDate"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm mới dự án (Sửa lỗi gạch đỏ ở dòng 59)
    public boolean insert(Project proj) {
        String sql = "INSERT INTO Project (projectName, startDate, endDate, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, proj.getProjectName());
            ps.setDate(2, proj.getStartDate());
            ps.setDate(3, proj.getEndDate());
            ps.setString(4, proj.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Xóa dự án (Sửa lỗi gạch đỏ ở dòng 33)
    public boolean delete(int id) {
        String sql = "DELETE FROM Project WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Project getById(int id) {
        String sql = "SELECT * FROM Project WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Project p = new Project();
                    p.setId(rs.getInt("id"));
                    p.setProjectName(rs.getNString("projectName"));
                    p.setStartDate(rs.getDate("startDate"));
                    p.setEndDate(rs.getDate("endDate"));
                    p.setDescription(rs.getNString("description"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Cập nhật thông tin dự án vào SQL Server
    public boolean update(Project p) {
        String sql = "UPDATE Project SET projectName = ?, startDate = ?, endDate = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, p.getProjectName());
            ps.setDate(2, p.getStartDate());
            ps.setDate(3, p.getEndDate());
            ps.setNString(4, p.getDescription());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
