/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import model.Assignment;
import util.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 *
 * @author mai09
 */
public class AssignmentDao {
    public boolean assignTask(Assignment a) {
        String checkSql = "SELECT COUNT(*) FROM Assignment WHERE task_id = ?";
        String updateSql = "UPDATE Assignment SET employee_id = ?, assigned_date = ? WHERE task_id = ?";
        String insertSql = "INSERT INTO Assignment (task_id, employee_id, assigned_date) VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, a.getTaskId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setInt(1, a.getEmployeeId());
                    updatePs.setDate(2, a.getAssignedDate());
                    updatePs.setInt(3, a.getTaskId());
                    return updatePs.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setInt(1, a.getTaskId());
                    insertPs.setInt(2, a.getEmployeeId());
                    insertPs.setDate(3, a.getAssignedDate());
                    return insertPs.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu Assignment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
