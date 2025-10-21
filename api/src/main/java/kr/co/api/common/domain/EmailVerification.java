package kr.co.api.common.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EmailVerification {
    
    private final String email;
    private final String verificationCode;
    private final LocalDateTime expiryTime;
    
    /**
     * 인증 코드가 만료되었는지 확인
     */
    public boolean isExpired() {
        return expiryTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * 인증 코드가 일치하는지 확인
     */
    public boolean matches(String code) {
        return verificationCode.equals(code);
    }
}