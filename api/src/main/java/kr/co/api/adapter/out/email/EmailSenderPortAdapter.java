package kr.co.api.adapter.out.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.co.api.application.port.out.email.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailSenderPortAdapter implements EmailSenderPort {

    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void sendEmail(String toEmail, String title, String content) throws Exception {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content, true); // true를 설정해서 HTML을 사용 가능하게 함
        helper.setReplyTo(username); // 회신 불가능한 주소 설정(답장 주소 설정)

        try {
            emailSender.send(message);
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, content);
            throw new RuntimeException("메일에러");
        }
    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}
