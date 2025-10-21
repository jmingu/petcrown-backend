package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {

    @Schema(description = "이메일", required = true)
    private String email;

    @Schema(description = "이름", required = true)
    private String name;

    @Schema(description = "닉네임", required = true)
    private String nickname;

    @Schema(description = "비밀번호", required = true)
    private String password;

    @Schema(description = "비밀번호 확인", required = true)
    private String passwordCheck;

    @Schema(description = "휴대폰 번호", required = true)
    private String phoneNumber;

    @Schema(description = "생년월일", required = true)
    private LocalDate birthDate;

    @Schema(description = "성별", required = true)
    private String gender;
}