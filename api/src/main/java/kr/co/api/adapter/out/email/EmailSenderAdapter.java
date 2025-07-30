package kr.co.api.adapter.out.email;

import kr.co.api.domain.port.out.SendEmailPort;
import kr.co.common.dto.EmailContentDto;
import kr.co.common.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 이메일 발송 어댑터 (Output Adapter)
 * SendEmailPort 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSenderAdapter implements SendEmailPort {
    
    private final EmailUtil emailUtil;
    
    @Override
    public void sendEmail(EmailContentDto emailContent) {
        try {
            emailUtil.sendEmail(emailContent);
            log.info("Email sent successfully to: {}", emailContent.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", emailContent.getTo(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
    
    @Override
    public void sendVerificationEmail(String email, String verificationCode) {
        EmailContentDto emailContent = EmailContentDto.builder()
                .to(email)
                .subject("[PetCrown] 이메일 인증 코드")
                .body(buildVerificationEmailBody(verificationCode))
                .build();
        
        sendEmail(emailContent);
    }
    
    @Override
    public void sendWelcomeEmail(String email, String userName) {
        EmailContentDto emailContent = EmailContentDto.builder()
                .to(email)
                .subject("[PetCrown] 회원가입을 환영합니다!")
                .body(buildWelcomeEmailBody(userName))
                .build();
        
        sendEmail(emailContent);
    }
    
    private String buildVerificationEmailBody(String verificationCode) {
        return String.format(
                "안녕하세요, PetCrown입니다.\n\n" +
                "이메일 인증을 위해 아래 인증 코드를 입력해주세요.\n\n" +
                "인증 코드: %s\n\n" +
                "감사합니다.",
                verificationCode
        );
    }
    
    private String buildWelcomeEmailBody(String userName) {
        return String.format(
                "안녕하세요, %s님!\n\n" +
                "PetCrown에 가입해주셔서 감사합니다.\n" +
                "반려동물과 함께하는 즐거운 시간을 만들어보세요!\n\n" +
                "감사합니다.",
                userName
        );
    }
}