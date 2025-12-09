package kr.co.api.game.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameScoreRegistrationRequestDto {

    @Schema(description = "게임 점수", required = true, example = "1234.5")
    private Double score;

    @Schema(description = "펫ID", required = true, example = "1")
    private Long petId;
}
