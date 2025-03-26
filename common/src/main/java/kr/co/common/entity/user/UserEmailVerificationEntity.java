package kr.co.common.entity.user;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_email_verification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserEmailVerificationEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailVerificationId;  // 이메일 인증 ID (자동 증가 PK)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // FK 참조

    private String verificationCode;  // 인증 코드

    private LocalDateTime expiresDate;  // 만료 시간 (ex: 10분 후)

    public UserEmailVerificationEntity(Long createUserId, Long updateUserId, Long emailVerificationId, UserEntity user, String verificationCode, LocalDateTime expiresDate) {
        super(createUserId, updateUserId);
        this.emailVerificationId = emailVerificationId;
        this.user = user;
        this.verificationCode = verificationCode;
        this.expiresDate = expiresDate;
    }
}
