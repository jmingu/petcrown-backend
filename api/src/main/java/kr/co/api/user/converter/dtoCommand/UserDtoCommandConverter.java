package kr.co.api.user.converter.dtoCommand;

import kr.co.api.user.dto.command.LoginTokenDto;
import kr.co.api.user.dto.command.PasswordResetDto;
import kr.co.api.user.dto.command.PasswordUpdateDto;
import kr.co.api.user.dto.command.UserInfoDto;
import kr.co.api.user.dto.command.UserRegistrationDto;
import kr.co.api.user.dto.command.UserUpdateDto;
import kr.co.api.user.dto.request.PasswordResetRequestDto;
import kr.co.api.user.dto.request.PasswordUpdateRequestDto;
import kr.co.api.user.dto.request.UserRegistrationRequestDto;
import kr.co.api.user.dto.request.UserUpdateRequestDto;
import kr.co.api.user.dto.response.LoginResponseDto;
import kr.co.api.user.dto.response.UserInfoResponseDto;
import org.springframework.stereotype.Component;

/**
 * User DTO와 Command DTO 간 양방향 변환을 담당하는 Converter
 * Request DTO -> Command DTO 변환과 Command DTO -> Response DTO 변환을 모두 담당
 */
@Component
public class UserDtoCommandConverter {

    // Request DTO -> Command DTO 변환 메서드들

    /**
     * UserRegistrationRequestDto를 UserRegistrationDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @return Service Layer에서 사용할 CommandDto
     */
    public UserRegistrationDto toCommandDto(UserRegistrationRequestDto request) {
        if (request == null) {
            return null;
        }

        return new UserRegistrationDto(
                request.getEmail(),
                request.getName(),
                request.getNickname(),
                request.getPassword(),
                request.getPasswordCheck()
        );
    }

    /**
     * UserUpdateRequestDto를 UserUpdateDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param userId 현재 로그인된 사용자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public UserUpdateDto toCommandDto(UserUpdateRequestDto request, Long userId) {
        if (request == null) {
            return null;
        }

        return new UserUpdateDto(
                userId,
                request.getName(),
                request.getNickname(),
                request.getGender(),
                request.getBirthDate(),
                request.getPhoneNumber()
        );
    }

    /**
     * PasswordUpdateRequestDto를 PasswordUpdateDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @param userId 현재 로그인된 사용자 ID
     * @return Service Layer에서 사용할 CommandDto
     */
    public PasswordUpdateDto toCommandDto(PasswordUpdateRequestDto request, Long userId) {
        if (request == null) {
            return null;
        }

        return new PasswordUpdateDto(
                userId,
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getNewPasswordConfirm()
        );
    }

    /**
     * PasswordResetRequestDto를 PasswordResetDto로 변환
     *
     * @param request HTTP 요청으로부터 받은 RequestDto
     * @return Service Layer에서 사용할 CommandDto
     */
    public PasswordResetDto toCommandDto(PasswordResetRequestDto request) {
        if (request == null) {
            return null;
        }

        return new PasswordResetDto(
                request.getEmail(),
                request.getName()
        );
    }

    // Command DTO -> Response DTO 변환 메서드들

    /**
     * LoginTokenDto를 LoginResponseDto로 변환
     *
     * @param loginTokenDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public LoginResponseDto toResponseDto(LoginTokenDto loginTokenDto) {
        if (loginTokenDto == null) {
            return null;
        }

        return new LoginResponseDto(
                loginTokenDto.getAccessToken(),
                loginTokenDto.getRefreshToken()
        );
    }

    /**
     * UserInfoDto를 UserInfoResponseDto로 변환
     *
     * @param userInfoDto Service Layer에서 반환된 CommandDto
     * @return HTTP 응답으로 보낼 ResponseDto
     */
    public UserInfoResponseDto toResponseDto(UserInfoDto userInfoDto) {
        if (userInfoDto == null) {
            return null;
        }

        return new UserInfoResponseDto(
                userInfoDto.getUserId(),
                userInfoDto.getEmail(),
                userInfoDto.getName(),
                userInfoDto.getNickname(),
                userInfoDto.getPhoneNumber(),
                userInfoDto.getProfileImageUrl(),
                userInfoDto.getBirthDate(),
                userInfoDto.getGender(),
                userInfoDto.getIsEmailVerified()
        );
    }
}