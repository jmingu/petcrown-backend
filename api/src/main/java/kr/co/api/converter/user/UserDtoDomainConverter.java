package kr.co.api.converter.user;

import kr.co.api.adapter.in.dto.user.request.UserEmailRegistrationRequestDto;
import kr.co.api.adapter.in.dto.user.request.UserInfoChangeRequestDto;
import kr.co.api.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDtoDomainConverter {


    /**
     * 회원가입 요청 DTO를 도메인 객체로 변환
     */
    public User registerRequestDtoToDomain(UserEmailRegistrationRequestDto dto) {
        return User.createUserByEmail(dto.getEmail(), dto.getName(), dto.getNickname(), dto.getPassword(), dto.getPasswordCheck(), dto.getPhoneNumber(), dto.getBirthDate() ,dto.getGender());
    }

    /**
     * 사용자 정보변경 요청 DTO를 도메인 객체로 변환
     */
    public User changeUserRequestDtoToDomain(UserInfoChangeRequestDto dto, Long userId) {
        return User.changeUser(userId, dto.getName(), dto.getNickname(), dto.getPhoneNumber(), dto.getBirthDate(), dto.getGender());

    }
}
