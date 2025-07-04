package kr.co.common.entity.user;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // protected 생성자 추가
@Getter
public class EmailVerificationEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailVerificationId;  // 이메일 인증 ID (자동 증가 PK)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // FK 참조

    private String verificationCode;  // 인증 코드

    private LocalDateTime expiresDate;  // 만료 시간 (ex: 10분 후)

    /**
     * 이메일 인증 엔티티를 생성하는 메서드
     */
    public static EmailVerificationEntity createEmailVerification(Long createUserId, Long updateUserId, Long emailVerificationId, UserEntity user, String verificationCode, LocalDateTime expiresDate) {
        return new EmailVerificationEntity(createUserId, updateUserId, emailVerificationId, user, verificationCode, expiresDate);
    }

    private EmailVerificationEntity(Long createUserId, Long updateUserId, Long emailVerificationId, UserEntity user, String verificationCode, LocalDateTime expiresDate) {
        super(createUserId, updateUserId);
        this.emailVerificationId = emailVerificationId;
        this.user = user;
        this.verificationCode = verificationCode;
        this.expiresDate = expiresDate;
    }
}
