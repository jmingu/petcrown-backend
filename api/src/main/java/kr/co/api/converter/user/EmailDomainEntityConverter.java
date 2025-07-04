package kr.co.api.converter.user;

import kr.co.api.domain.model.user.Email;
import kr.co.common.entity.user.EmailVerificationEntity;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailDomainEntityConverter {

    public EmailVerificationEntity EmailToUserEmailVerificationEntity(Email email, UserEntity userEntity) {
        return EmailVerificationEntity.createEmailVerification(
                userEntity.getUserId(), userEntity.getUserId(), email.getEmailVerificationId(), userEntity, email.getVerificationCode(), email.getExpiresDate()
        );
    }

    /**
     * entity -> Email변환
     */
    public Email userEmailVerificationEntityToEmail(EmailVerificationEntity entity) {
        return Email.getEmailAllFiled(
                entity.getEmailVerificationId(), null, entity.getVerificationCode(), entity.getExpiresDate()
        );

    }
}
