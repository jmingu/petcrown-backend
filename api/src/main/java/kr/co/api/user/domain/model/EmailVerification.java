package kr.co.api.user.domain.model;

import kr.co.common.exception.PetCrownException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Random;

import static kr.co.common.enums.BusinessCode.*;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EmailVerification {
    
    private final Long emailVerificationId;
    private final User user;
    private final String verificationCode;
    private final LocalDateTime expiresDate;
    
    /**
     * 기존 데이터로부터 EmailVerification 생성
     */
    public static EmailVerification createVerifacationCode(Long emailVerificationId, User user,
                                     String verificationCode, LocalDateTime expiresDate) {
        return new EmailVerification(emailVerificationId, user, verificationCode, 
                                   expiresDate);
    }
    
    /**
     * 이메일 인증 도메인 생성 (회원가입용)
     */
    public static EmailVerification createForRegistration(User user) {
        String verificationCode = generateVerificationCode();
        LocalDateTime expiresDate = LocalDateTime.now().plusMinutes(10); // 10분 후 만료
        
        return new EmailVerification(
                null,
                user,
                verificationCode,
                expiresDate
        );
    }
    
    /**
     * 이메일 인증 코드 생성 (6자리 랜덤 숫자)
     */
    private static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
    
    /**
     * 인증 코드 검증 (도메인 로직)
     */
    public void verifyCode(String inputCode) {
        if(inputCode == null) {
            throw new PetCrownException(VERIFICATION_CODE_NOT_FOUND);
        }
        if (isExpired()) {
            throw new PetCrownException(AUTH_CODE_EXPIRED);
        }
        
        if (!this.verificationCode.equals(inputCode)) {
            throw new PetCrownException(AUTH_CODE_INVALID);
        }
    }
    
    /**
     * 인증 코드 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresDate);
    }
    
    /**
     * 이메일 값 조회
     */
    public String getEmailValue() {
        return this.user.getEmail().getValue();
    }
    
    /**
     * 사용자 ID 조회
     */
    public Long getUserId() {
        return this.user.getUserId();
    }
}