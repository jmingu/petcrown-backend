package kr.co.api.converter.user;

import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public UserEntity toEntity(User user) {

        return new UserEntity(null, user.getEmail(), user.getName(), user.getPassword());
    }
}
