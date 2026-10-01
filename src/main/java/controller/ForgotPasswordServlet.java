package controller;

import dao.AccountDao;
import model.Account;
import util.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {
    private AccountDao accountDao = new AccountDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        // BƯỚC 1: Người dùng nhập username và nhấn Yêu cầu xác thực
        if ("sendOtp".equals(action)) {
            String username = request.getParameter("username");
            Account acc = accountDao.getByUsername(username);

            if (acc == null) {
                request.setAttribute("error", "Tài khoản không tồn tại!");
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
                return;
            }

            session.setAttribute("reset_user", acc);

            // Nếu là ADMIN -> Sinh OTP và gửi về email cố định
            if ("ADMIN".equalsIgnoreCase(acc.getRole())) {
                String otp = String.format("%06d", new Random().nextInt(999999));
                session.setAttribute("admin_otp", otp);
                session.setAttribute("otp_time", System.currentTimeMillis());

                EmailService.sendOtp(EmailService.ADMIN_EMAIL, otp);

                request.setAttribute("step", "verifyOtp");
                request.setAttribute("msg", "Mã OTP đã được gửi đến email " + EmailService.ADMIN_EMAIL);
            } else {
                // Nếu là EMPLOYEE -> Cho phép đổi trực tiếp không cần OTP
                request.setAttribute("step", "resetPass");
            }

            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            return;
        }

        // BƯỚC 2: Xác nhận OTP và đặt lại mật khẩu
        if ("confirmReset".equals(action)) {
            Account acc = (Account) session.getAttribute("reset_user");
            if (acc == null) {
                response.sendRedirect(request.getContextPath() + "/forgot-password");
                return;
            }

            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                request.setAttribute("step", "ADMIN".equalsIgnoreCase(acc.getRole()) ? "verifyOtp" : "resetPass");
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
                return;
            }

            // Kiểm tra OTP nếu là ADMIN
            if ("ADMIN".equalsIgnoreCase(acc.getRole())) {
                String inputOtp = request.getParameter("otp");
                String sessionOtp = (String) session.getAttribute("admin_otp");

                if (sessionOtp == null || !sessionOtp.equals(inputOtp)) {
                    request.setAttribute("error", "Mã OTP không chính xác hoặc đã hết hạn!");
                    request.setAttribute("step", "verifyOtp");
                    request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
                    return;
                }
            }

            // Tiến hành cập nhật
            if (accountDao.resetPassword(acc.getUsername(), newPassword)) {
                session.removeAttribute("reset_user");
                session.removeAttribute("admin_otp");

                request.setAttribute("success", "Đặt lại mật khẩu thành công! Hãy đăng nhập lại.");
                request.getRequestDispatcher("/login.jsp?role=" + acc.getRole()).forward(request, response);
            } else {
                request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            }
        }
    }
}