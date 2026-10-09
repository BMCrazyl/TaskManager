/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author mai09
 */
public class DBConnect {
    private static final String URL = "jdbc:sqlserver://PC-35\\SQLEXPRESS:1433;databaseName=task_management_db;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "123456"; // Đổi theo mật khẩu MySQL trên máy của bạn

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Kết nối CSDL thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy JDBC Driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection testConn = DBConnect.getConnection();
        if (testConn != null) {
            System.out.println("Sẵn sàng thao tác dữ liệu!");
            try {
                testConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Kết nối thất bại. Vui lòng kiểm tra lại thông tin cấu hình.");
        }
    }
}
