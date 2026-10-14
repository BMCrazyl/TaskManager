package dao;

import model.Assignment;
import util.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AssignmentDao {
    public boolean assignTask(Assignment a) {
        String findSql = "SELECT employee_id FROM Assignment WHERE task_id = ?";
        String updateSql = "UPDATE Assignment SET employee_id = ?, assigned_date = ? WHERE task_id = ?";
        String insertSql = "INSERT INTO Assignment (task_id, employee_id, assigned_date) VALUES (?, ?, ?)";
        String reopenSql = "UPDATE Task SET status = N'Đang thực hiện' WHERE id = ? AND status = N'Hoàn thành'";
        String deleteAttendanceSql = "DELETE FROM AttendanceLog WHERE task_id = ?";

        if (!new TaskDao().ensureAttendanceTableExists()) {
            return false;
        }

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer previousEmployeeId = null;
                try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                    ps.setInt(1, a.getTaskId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) previousEmployeeId = rs.getInt("employee_id");
                    }
                }

                if (previousEmployeeId == null) {
                    try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                        ps.setInt(1, a.getTaskId());
                        ps.setInt(2, a.getEmployeeId());
                        ps.setDate(3, a.getAssignedDate());
                        if (ps.executeUpdate() != 1) { conn.rollback(); return false; }
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                        ps.setInt(1, a.getEmployeeId());
                        ps.setDate(2, a.getAssignedDate());
                        ps.setInt(3, a.getTaskId());
                        if (ps.executeUpdate() < 1) { conn.rollback(); return false; }
                    }

                    if (previousEmployeeId.intValue() != a.getEmployeeId()) {
                        try (PreparedStatement ps = conn.prepareStatement(reopenSql)) {
                            ps.setInt(1, a.getTaskId());
                            ps.executeUpdate();
                        }
                        try (PreparedStatement ps = conn.prepareStatement(deleteAttendanceSql)) {
                            ps.setInt(1, a.getTaskId());
                            ps.executeUpdate();
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu Assignment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
