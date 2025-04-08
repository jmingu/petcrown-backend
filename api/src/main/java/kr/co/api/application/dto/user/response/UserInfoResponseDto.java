package kr.co.api.application.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoResponseDto {
    private String email;
    private String name;
    private String nickname;
    private String phoneNumber;
    private String profileImageUrl;
    private LocalDate birthDate;
    private String gender;
}
