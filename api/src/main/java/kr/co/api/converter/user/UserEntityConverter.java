package kr.co.api.converter.user;

import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public UserEntity emailResistrsationToEntity(User user) {

        return new UserEntity(0L,0L,"N", null, user.getEmail(), user.getUserUuid(), user.getPassword(), user.getRole(), user.getName(), user.getNickname(), user.getPhoneNumber(), user.getProfileImageUrl(), user.getBirthDate(), user.getGender(), user.getLoginType(), user.getLoginId(), user.getIsEmailVerified(), user.getCompany());
    }

    public User toDomain(UserEntity userEntity) {

        return new User(userEntity.getEmail(), userEntity.getName(), userEntity.getNickname(), userEntity.getPassword());
    }
}
