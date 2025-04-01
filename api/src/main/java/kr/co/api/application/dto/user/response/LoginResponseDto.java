package kr.co.api.application.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
