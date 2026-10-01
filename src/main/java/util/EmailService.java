package util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    // Địa chỉ nhận OTP quản lý
    public static final String ADMIN_EMAIL = "hovophuctan1403@gmail.com";
    
    // Nếu có App Password Gmail thì điền vào đây, nếu để trống hệ thống sẽ in trực tiếp OTP ra console NetBeans
    private static final String SENDER_EMAIL = "hovophuctan1403@gmail.com";
    private static final String APP_PASSWORD = ""; 

    public static boolean sendOtp(String toEmail, String otp) {
        System.out.println("==================================================");
        System.out.println(">>> [HỆ THỐNG XÁC THỰC OTP QUẢN LÝ]");
        System.out.println(">>> Gửi đến email: " + toEmail);
        System.out.println(">>> MÃ OTP XÁC THỰC CỦA BẠN LÀ: " + otp);
        System.out.println("==================================================");

        if (APP_PASSWORD.isEmpty()) {
            return true; // Chế độ console test
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Task Manager Security"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã xác thực OTP đặt lại mật khẩu Quản Lý");
            message.setText("Xin chào,\n\nMã OTP xác nhận đặt lại mật khẩu của bạn là: " + otp 
                          + "\n\nMã có hiệu lực trong 5 phút. Vui lòng không chia sẻ cho bất kỳ ai.");
            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Gửi mail thất bại: " + e.getMessage());
            return true; 
        }
    }
}