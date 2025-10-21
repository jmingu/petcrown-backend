package kr.co.api.user.converter.domainEntity;

import kr.co.api.user.domain.model.EmailVerification;
import kr.co.common.entity.user.EmailVerificationEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmailVerificationDomainEntityConverter {

    /**
     * EmailVerification 도메인 → EmailVerificationEntity 변환
     */
    public EmailVerificationEntity toEntity(EmailVerification domain) {
        if (domain == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        return new EmailVerificationEntity(
                domain.getEmailVerificationId(),
                domain.getUser().getUserId(),
                domain.getVerificationCode(),
                domain.getExpiresDate(),
                now,
                domain.getUser().getUserId(),
                now,
                domain.getUser().getUserId());
    }

    /**
     * EmailVerificationEntity → EmailVerification 도메인 변환
     */
//    public EmailVerification toDomain(EmailVerificationEntity entity, User user) {
//        if (entity == null) {
//            return null;
//        }
//
//        return new EmailVerification(
//                entity.getEmailVerificationId(),
//                user,
//                entity.getVerificationCode(),
//                entity.getExpiresDate()
//        );
//    }
}