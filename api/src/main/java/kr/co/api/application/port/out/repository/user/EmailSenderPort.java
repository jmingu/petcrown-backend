package kr.co.api.application.port.out.repository.user;

public interface EmailSenderPort {
    void sendEmail(String toEmail, String title, String text) throws Exception;
}
