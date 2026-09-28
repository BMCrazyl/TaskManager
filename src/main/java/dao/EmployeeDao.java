/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.Employee;
import util.DBConnect;
import java.sql.*;
import java.util.*;
/**
 *
 * @author mai09
 */
public class EmployeeDao {
    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employee ORDER BY id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("position")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Employee emp) {
        String sql = "INSERT INTO Employee (name, email, phone, position) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getPosition());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Employee WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Employee getById(int id) {
        String sql = "SELECT * FROM Employee WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("name"));
                e.setEmail(rs.getString("email"));
                e.setPhone(rs.getString("phone"));
                e.setPosition(rs.getString("position"));
                return e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Cập nhật thông tin nhân viên vào SQL Server
    public boolean update(Employee emp) {
        String sql = "UPDATE Employee SET name = ?, email = ?, phone = ?, position = ? WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getPosition());
            ps.setInt(5, emp.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
