package kr.co.api.adapter.in.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationCodeRequestDto {

    @Schema(description = "인증코드", required = true, example = "123456")
    private String code;

    @Schema(description = "이메일", required = true, example = "example@example.com")
    private String email;

}
