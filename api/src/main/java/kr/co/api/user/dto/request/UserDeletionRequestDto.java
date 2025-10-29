package kr.co.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletionRequestDto {

    @Schema(description = "이메일", required = true, example = "user@example.com")
    private String email;

    @Schema(description = "이름", required = true, example = "홍길동")
    private String name;

    @Schema(description = "비밀번호", required = true, example = "password123")
    private String password;
}
