package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 비밀번호 찾기 Request DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDto {

    @Schema(description = "이메일", required = true, example = "user@example.com")
    private String email;

    @Schema(description = "이름", required = true, example = "홍길동")
    private String name;
}
