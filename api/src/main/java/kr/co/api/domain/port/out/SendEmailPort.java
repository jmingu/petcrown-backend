package kr.co.api.domain.port.out;

import kr.co.common.dto.EmailContentDto;

/**
 * 이메일 발송 출력 포트 (Secondary Port)
 * 외부 이메일 서비스와의 통신 인터페이스
 */
public interface SendEmailPort {
    
    void sendEmail(EmailContentDto emailContent);
    
    void sendVerificationEmail(String email, String verificationCode);
    
    void sendWelcomeEmail(String email, String userName);
}