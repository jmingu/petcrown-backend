package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequestDto {

    @Schema(description = "엑세스토큰", required = true)
    private String accessToken;

    @Schema(description = "리프레쉬토큰", required = true)
    private String refreshToken;
}
