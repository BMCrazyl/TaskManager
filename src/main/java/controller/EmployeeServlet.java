package controller;

import dao.EmployeeDao;
import model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "EmployeeServlet", urlPatterns = {"/employees"})
public class EmployeeServlet extends HttpServlet {
    private final EmployeeDao employeeDao = new EmployeeDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");
        String keyword = request.getParameter("keyword");

        if ("edit".equals(action) && idParam != null && !idParam.trim().isEmpty()) {
            try { request.setAttribute("employee", employeeDao.getById(Integer.parseInt(idParam))); }
            catch (NumberFormatException e) { response.sendError(400, "Mã nhân viên không hợp lệ."); return; }
        } else if ("delete".equals(action) && idParam != null && !idParam.trim().isEmpty()) {
            try { employeeDao.delete(Integer.parseInt(idParam)); }
            catch (NumberFormatException e) { response.sendError(400, "Mã nhân viên không hợp lệ."); return; }
            response.sendRedirect(request.getContextPath() + "/employees");
            return;
        }
        request.setAttribute("searchKeyword", keyword == null ? "" : keyword);
        request.setAttribute("employeeList", employeeDao.searchEmployees(keyword));
        request.getRequestDispatcher("/employee-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String idStr=request.getParameter("id"), name=request.getParameter("name"),
               email=request.getParameter("email"), phone=request.getParameter("phone"),
               position=request.getParameter("position");
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()
                || position == null || position.trim().isEmpty()) {
            response.sendError(400, "Họ tên, email và chức vụ là các trường bắt buộc."); return;
        }
        Employee emp = new Employee();
        emp.setName(name.trim()); emp.setEmail(email.trim());
        emp.setPhone(phone == null ? "" : phone.trim()); emp.setPosition(position.trim());
        boolean saved;
        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                emp.setId(Integer.parseInt(idStr)); saved=employeeDao.update(emp);
            } else saved=employeeDao.insert(emp);
        } catch (NumberFormatException e) { response.sendError(400, "Mã nhân viên không hợp lệ."); return; }
        if (!saved) {
            request.setAttribute("formError", "Không lưu được nhân viên. Kiểm tra email có bị trùng hay không.");
            request.setAttribute("employee", emp);
            request.setAttribute("employeeList", employeeDao.getAll());
            request.getRequestDispatcher("/employee-list.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/employees");
    }
}
