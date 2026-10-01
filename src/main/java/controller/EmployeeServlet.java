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
        String idParam = request.getParameter("id");

        // 1. No pinidut ti buton a Sukatan (Edit)
        if ("edit".equals(action) && idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                Employee emp = employeeDao.getById(id); 
                request.setAttribute("employee", emp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        // 2. No pinidut ti buton a Punasen (Delete)
        else if ("delete".equals(action) && idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                employeeDao.delete(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/employees");
            return;
        }

        // Kankanayon nga ikabil ti listaan dagiti empleado para iti table
        request.setAttribute("employeeList", employeeDao.getAll());
        request.getRequestDispatcher("/employee-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

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

        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                // UPDATE no adda dati nga ID
                emp.setId(Integer.parseInt(idStr));
                employeeDao.update(emp);
            } else {
                // INSERT no baro nga empleado
                employeeDao.insert(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/employees");
    }
}