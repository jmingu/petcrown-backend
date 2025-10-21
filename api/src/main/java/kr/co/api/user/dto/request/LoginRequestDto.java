package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @Schema(description = "이메일", required = true)
    private String email;
    
    @Schema(description = "비밀번호", required = true)
    private String password;
}