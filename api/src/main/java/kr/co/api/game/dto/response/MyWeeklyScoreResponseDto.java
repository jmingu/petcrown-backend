package kr.co.api.game.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.api.game.dto.command.GameScoreDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 내 주간 최대 점수 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyWeeklyScoreResponseDto {

    @Schema(description = "주 시작일", example = "2025-01-20")
    private LocalDate weekStartDate;

    @Schema(description = "내 점수", example = "5432.1")
    private Double score;
    @Schema(description = "닉네임", example = "닉네임")

    private String nickname;
    @Schema(description = "펫이름", example = "게이머123")
    private String name;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;
}
