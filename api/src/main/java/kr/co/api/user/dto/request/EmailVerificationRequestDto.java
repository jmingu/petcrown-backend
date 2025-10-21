package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequestDto {

    @Schema(description = "이메일", required = true)
    private String email;

    @Schema(description = "인증코드", required = true)
    private String code;
}