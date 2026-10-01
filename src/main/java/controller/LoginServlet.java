package controller;

import dao.AccountDao;
import model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private AccountDao accountDao = new AccountDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        String expectedRole = request.getParameter("expectedRole");

        Account acc = accountDao.checkLogin(u, p);
        if (acc != null) {
            // Kiểm tra xem đăng nhập có đúng cổng vai trò đã chọn hay không
            if (expectedRole != null && !expectedRole.isEmpty() && !expectedRole.equalsIgnoreCase(acc.getRole())) {
                request.setAttribute("error", "Tài khoản không thuộc quyền " 
                    + ("ADMIN".equalsIgnoreCase(expectedRole) ? "Quản Lý!" : "Nhân Viên!"));
                request.getRequestDispatcher("/login.jsp?role=" + expectedRole).forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("user", acc);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("/login.jsp" + (expectedRole != null ? "?role=" + expectedRole : "")).forward(request, response);
        }
    }
}