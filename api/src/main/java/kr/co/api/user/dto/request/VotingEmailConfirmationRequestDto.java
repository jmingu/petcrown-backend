package kr.co.api.user.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VotingEmailConfirmationRequestDto {

    @Schema(description = "인증할 이메일", required = true, example = "user@example.com")
    private String email;

    @Schema(description = "암호화된 인증 토큰", required = true, example = "encrypted_token")
    private String encryptedToken;
}