package kr.co.api.converter.user;

import kr.co.api.adapter.in.dto.user.UserRegistrationRequestDto;
import kr.co.api.domain.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public User registerRequestDtoToDomain(UserRegistrationRequestDto dto) {
        return new User(dto.getEmail(), dto.getName(), dto.getNickname(), dto.getPassword(), dto.getPasswordCheck());
    }
}
