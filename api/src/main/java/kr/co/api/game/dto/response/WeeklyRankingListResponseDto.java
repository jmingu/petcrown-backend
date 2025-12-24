package kr.co.api.game.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 주간 랭킹 리스트 응답 DTO (리스트를 필드로 감싸기)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyRankingListResponseDto {

    @Schema(description = "주간 랭킹 목록")
    private List<RankingItemDto> rankings;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingItemDto {

        @Schema(description = "스코어 ID", example = "1")
        private Long scoreId;

        @Schema(description = "순위", example = "1")
        private Integer ranking;

        @Schema(description = "닉네임", example = "게이머123")
        private String nickname;

        @Schema(description = "점수", example = "9876.5")
        private Double score;

        @Schema(description = "펫이름", example = "게이머123")
        private String name;

        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
        private String imageUrl;
    }
}
