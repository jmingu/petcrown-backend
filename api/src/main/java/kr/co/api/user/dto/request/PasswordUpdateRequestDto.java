package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequestDto {

    @Schema(description = "현재 비밀번호", required = true)
    private String currentPassword;

    @Schema(description = "새 비밀번호", required = true)
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", required = true)
    private String newPasswordConfirm;
}