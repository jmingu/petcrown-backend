package kr.co.api.adapter.in.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailRegistrationRequestDto {

    @Schema(description = "이메일", required = true, example = "example@example.com")
    private String email;

    @Schema(description = "이름", required = true, example = "홍길동")
    private String name;

    @Schema(description = "닉네임", required = true, example = "닉네임")
    private String nickname;

    @Schema(description = "비밀번호", required = true, example = "P@ssw0rd123")
    private String password;

    @Schema(description = "비밀번호 확인", required = true, example = "P@ssw0rd123")
    private String passwordCheck;

    @Schema(description = "핸드폰 번호", required = true, example = "01012345678")
    private String phoneNumber;

    @Schema(description = "생년월일", required = true, example = "19990101")
    private String birthDate;

    @Schema(description = "성별", required = true, example = "M")
    private String gender;
}
