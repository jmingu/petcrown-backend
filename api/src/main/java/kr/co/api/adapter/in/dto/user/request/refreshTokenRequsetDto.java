package kr.co.api.adapter.in.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class refreshTokenRequsetDto {
    @Schema(description = "리프래쉬 토큰", required = true, example = "refreshToken")
    private String refreshToken;

}
