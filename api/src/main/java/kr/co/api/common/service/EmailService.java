package kr.co.api.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender javaMailSender;
    
    /**
     * 이메일 인증 코드 발송 (비동기)
     */
    @Async
    public void sendVerificationEmailAsync(String toEmail, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[PetCrown] 이메일 인증 코드");
            message.setText(
                "안녕하세요!\n\n" +
                "PetCrown 이메일 인증 코드입니다.\n\n" +
                "인증 코드: " + verificationCode + "\n\n" +
                "이 코드는 5분 후 만료됩니다.\n\n" +
                "감사합니다."
            );
            
            javaMailSender.send(message);
            log.info("Verification email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
        }
    }
    
    /**
     * 환영 이메일 발송 (비동기)
     */
    @Async
    public void sendWelcomeEmailAsync(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[PetCrown] 회원가입을 축하합니다!");
            message.setText(
                "안녕하세요 " + userName + "님!\n\n" +
                "PetCrown에 가입해 주셔서 감사합니다.\n\n" +
                "이제 귀여운 반려동물들과 함께 즐거운 시간을 보내세요!\n\n" +
                "PetCrown 팀 드림"
            );
            
            javaMailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }

    /**
     * HTML 이메일 발송
     */
    @Async
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML 형식

            javaMailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 임시 비밀번호 이메일 발송 (비동기)
     */
    @Async
    public void sendTemporaryPasswordEmailAsync(String toEmail, String temporaryPassword) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = kr.co.common.util.EmailUtil.generateTemporaryPasswordEmail(temporaryPassword).getBody();

            helper.setTo(toEmail);
            helper.setSubject("[PetCrown] 임시 비밀번호 발급");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Temporary password email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send temporary password email to: {}", toEmail, e);
        }
    }

}