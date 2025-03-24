package kr.co.api.application.port.out.email;

public interface Email {
    void sendEmail(String toEmail, String title, String text);
}
