package kr.co.api.adapter.in.dto.vote.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VoteRegistrationRequestDto {
    @Schema(description = "펫ID", required = true, example = "0")
    private Long petId;

    @Schema(description = "프로필 이미지 URL", required = true, example = "https://example.com/profile.jpg")
    private String profileImageUrl;
}
