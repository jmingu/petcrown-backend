package kr.co.api.application.port.out.email;

public interface EmailSenderPort {
    void sendEmail(String toEmail, String title, String text);
}
