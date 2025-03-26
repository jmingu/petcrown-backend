package kr.co.api.converter.user;

import kr.co.api.domain.model.user.Password;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public UserEntity userToEntity(User user) {

        return new UserEntity(user.getUserId(),user.getUserId(),"N", null, user.getEmail(), user.getUserUuid(), user.getPassword(), user.getRole(), user.getName(), user.getNickname(), user.getPhoneNumber(), user.getProfileImageUrl(), user.getBirthDate(), user.getGender(), user.getLoginType(), user.getLoginId(), user.getIsEmailVerified(), user.getCompany());
    }

    public User toDomain(UserEntity userEntity) {

        return new User(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getUserUuid(),
                userEntity.getName(),
                userEntity.getNickname(),
                new Password(userEntity.getPassword()),
                userEntity.getRole(),
                userEntity.getPhoneNumber(),
                userEntity.getProfileImageUrl(),
                userEntity.getBirthDate(),
                userEntity.getGender(),
                userEntity.getLoginType(),
                userEntity.getLoginId(),
                userEntity.getIsEmailVerified(),
                userEntity.getCompanyEntity()
        );
    }
}
