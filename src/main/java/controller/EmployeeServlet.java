/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.EmployeeDao;
import model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author mai09
 */
@WebServlet(name = "EmployeeServlet", urlPatterns = {"/employees"})
public class EmployeeServlet extends HttpServlet {
    private EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        
        // Nếu bấm nút Sửa: lấy dữ liệu nhân viên đẩy lên form
        if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Employee emp = employeeDao.getById(id);
            request.setAttribute("employee", emp);
        }

        // Luôn load danh sách nhân viên
        request.setAttribute("employeeList", employeeDao.getAll());
        request.getRequestDispatcher("/employee-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String position = request.getParameter("position");

        Employee emp = new Employee();
        emp.setName(name);
        emp.setEmail(email);
        emp.setPhone(phone);
        emp.setPosition(position);

        if (idStr != null && !idStr.trim().isEmpty()) {
            // Trường hợp cập nhật (UPDATE)
            emp.setId(Integer.parseInt(idStr));
            employeeDao.update(emp);
        } else {
            // Trường hợp thêm mới (INSERT)
            employeeDao.insert(emp);
        }
        response.sendRedirect("employees");
    }
}