package kr.co.api.adapter.in.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequsetDto {
    @Schema(description = "이메일", required = true, example = "example@example.com")
    private String email;

    @Schema(description = "비밀번호", required = true, example = "P@ssw0rd123")
    private String password;
}
