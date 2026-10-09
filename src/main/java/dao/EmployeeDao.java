package dao;

import model.Employee;
import util.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employee ORDER BY id DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(readEmployee(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Employee> searchEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAll();
        List<Employee> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = "SELECT * FROM Employee WHERE name LIKE ? OR email LIKE ? " +
                     "OR ISNULL(phone, '') LIKE ? OR position LIKE ? ORDER BY id DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern); ps.setNString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(readEmployee(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Employee emp) {
        String sql = "INSERT INTO Employee (name, email, phone, position) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, emp.getName()); ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone()); ps.setNString(4, emp.getPosition());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Employee WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public Employee getById(int id) {
        String sql = "SELECT * FROM Employee WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return readEmployee(rs); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean update(Employee emp) {
        String sql = "UPDATE Employee SET name = ?, email = ?, phone = ?, position = ? WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, emp.getName()); ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone()); ps.setNString(4, emp.getPosition()); ps.setInt(5, emp.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private Employee readEmployee(ResultSet rs) throws SQLException {
        return new Employee(rs.getInt("id"), rs.getNString("name"), rs.getString("email"),
                            rs.getString("phone"), rs.getNString("position"));
    }
}
