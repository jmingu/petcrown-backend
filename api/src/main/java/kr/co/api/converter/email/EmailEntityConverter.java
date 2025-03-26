package kr.co.api.converter.email;

import kr.co.api.converter.user.UserEntityConverter;
import kr.co.api.domain.model.Email;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEmailVerificationEntity;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailEntityConverter {

    public UserEmailVerificationEntity EmailToUserEmailVerificationEntity(Email email, UserEntity userEntity) {

        return new UserEmailVerificationEntity(userEntity.getUserId(), userEntity.getUserId(), email.getEmailVerificationId(), userEntity, email.getVerificationCode(), email.getExpiresDate());
    }


    public Email userEmailVerificationEntityToEmail(UserEmailVerificationEntity entity) {
        return new Email(entity.getEmailVerificationId(), null, entity.getVerificationCode(), entity.getExpiresDate());

    }

}
