package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Schema
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequestDto {

    @Schema(description = "비밀번호", required = true)
    private String password;

    @Schema(description = "비밀번호 확인", required = true)
    private String passwordCheck;
}
