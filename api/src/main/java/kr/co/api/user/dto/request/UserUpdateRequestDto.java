package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

    @Schema(description = "이름", required = true)
    private String name;

    @Schema(description = "닉네임", required = true)
    private String nickname;

    @Schema(description = "성별", required = true)
    private String gender;

    @Schema(description = "생년월일", required = true)
    private LocalDate birthDate;

    @Schema(description = "휴대폰 번호", required = true)
    private String phoneNumber;



}