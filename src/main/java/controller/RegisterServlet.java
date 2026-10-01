package controller;

import dao.AccountDao;
import model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private AccountDao accountDao = new AccountDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        String cp = request.getParameter("confirmPassword");
        String fullname = request.getParameter("fullname");

        if (!p.equals(cp)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (accountDao.checkUsernameExists(u)) {
            request.setAttribute("error", "Tên tài khoản này đã được sử dụng!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Khóa chặt role: Chỉ tạo tài khoản Nhân Viên (EMPLOYEE)
        Account acc = new Account(u, p, fullname, "EMPLOYEE");
        if (accountDao.register(acc)) {
            request.setAttribute("success", "Đăng ký thành công! Hãy đăng nhập vào cổng Nhân viên.");
            request.getRequestDispatcher("/login.jsp?role=EMPLOYEE").forward(request, response);
        } else {
            request.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}