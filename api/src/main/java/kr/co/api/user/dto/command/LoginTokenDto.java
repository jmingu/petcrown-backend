package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginTokenDto {
    private final String accessToken;
    private final String refreshToken;
}
