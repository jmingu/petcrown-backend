package kr.co.api.domain.model.user;

import kr.co.api.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
public class Email {
    private Long emailVerificationId;
    private User user;
    private String verificationCode;   // 인증 코드
    private LocalDateTime expiresDate;  // 만료 시간

    /**
     * 회원가입 시 이메일 생성자 메서드
     */
    public static Email crateEmail(User user) {

        // 6자리 인증 코드 생성
        String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));
        // 인증시간 10분
        LocalDateTime expiresDate = LocalDateTime.now().plusMinutes(10);

        return new Email(null, user, verificationCode, expiresDate);
    }

    /**
     * 모든 필드로 생성하는 메서드
     */
    public static Email getEmailAllFiled(Long emailVerificationId, User user, String verificationCode, LocalDateTime expiresDate) {
        return new Email(emailVerificationId, user, verificationCode, expiresDate);
    }


    public Email(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }


    // 인증 코드 비교
    public boolean isVerificationCodeValid(String code) {
        // 전달된 인증 코드가 저장된 인증 코드와 일치하는지 비교
        return verificationCode != null && verificationCode.equals(code);
    }

    // 만료 시간 비교
    public boolean isExpired() {
        // 만료 시간이 현재시간보다 이전인가 비교
        return expiresDate != null && expiresDate.isBefore(LocalDateTime.now());
    }




}


