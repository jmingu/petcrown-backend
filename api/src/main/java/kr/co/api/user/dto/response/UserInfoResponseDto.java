package kr.co.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {
    
    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private String phoneNumber;
    private String profileImageUrl;
    private LocalDate birthDate;
    private String gender;
    private String isEmailVerified;
}