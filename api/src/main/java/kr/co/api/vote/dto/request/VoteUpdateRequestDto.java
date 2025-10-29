package kr.co.api.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VoteUpdateRequestDto {

    @Schema(description = "펫ID", required = true, example = "1")
    private Long petId;

    @Schema(description = "프로필 이미지 URL", required = false, example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "펫 감정 모드 ID", required = true, example = "1")
    private Integer petModeId;
}