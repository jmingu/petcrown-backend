package kr.co.api.converter.user;

import kr.co.api.adapter.in.dto.user.UserEmailRegistrationRequestDto;
import kr.co.api.domain.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public User registerRequestDtoToDomain(UserEmailRegistrationRequestDto dto) {
        return User.createUserByEmail(dto.getEmail(), dto.getName(), dto.getNickname(), dto.getPassword(), dto.getPasswordCheck(), dto.getPhoneNumber(), dto.getBirthDate() ,dto.getGender());
    }
}
