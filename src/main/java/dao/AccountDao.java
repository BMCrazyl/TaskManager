/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.Account;
import util.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 *
 * @author PC_32
 */
public class AccountDao {
    static {
        initTable();
    }

    private static void initTable() {
        String createTableSql = 
            "IF OBJECT_ID('Account', 'U') IS NULL " +
            "BEGIN " +
            "    CREATE TABLE Account ( " +
            "        id INT IDENTITY(1,1) PRIMARY KEY, " +
            "        username VARCHAR(50) UNIQUE NOT NULL, " +
            "        password VARCHAR(100) NOT NULL, " +
            "        fullname NVARCHAR(100) NOT NULL, " +
            "        role VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE' " +
            "    ); " +
            "    INSERT INTO Account (username, password, fullname, role) VALUES " +
            "    ('admin', '123456', N'Quản Lý Trưởng', 'ADMIN'), " +
            "    ('tanphuc', '123456', N'Tấn Hồ Võ Phúc', 'EMPLOYEE'); " +
            "END";

        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            System.out.println(">>> ĐÃ KHỞI TẠO BẢNG ACCOUNT VÀ DỮ LIỆU MẪU THÀNH CÔNG TỪ CODE!");
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo bảng Account: " + e.getMessage());
        }
    }

    // Kiểm tra đăng nhập
    public Account checkLogin(String username, String password) {
        String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getInt("id"));
                    acc.setUsername(rs.getString("username"));
                    acc.setPassword(rs.getString("password"));
                    acc.setFullname(rs.getNString("fullname"));
                    acc.setRole(rs.getString("role"));
                    return acc;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra tài khoản đã tồn tại chưa
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT id FROM Account WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đăng ký tài khoản mới
    public boolean register(Account acc) {
        String sql = "INSERT INTO Account (username, password, fullname, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setNString(3, acc.getFullname());
            ps.setString(4, acc.getRole());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Cập nhật lại mật khẩu mới theo tên tài khoản
    public boolean resetPassword(String username, String newPassword) {
        String sql = "UPDATE Account SET password = ? WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Lấy thông tin tài khoản theo tên đăng nhập
    public Account getByUsername(String username) {
        String sql = "SELECT * FROM Account WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getInt("id"));
                    acc.setUsername(rs.getString("username"));
                    acc.setPassword(rs.getString("password"));
                    acc.setFullname(rs.getNString("fullname"));
                    acc.setRole(rs.getString("role"));
                    return acc;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
