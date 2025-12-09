package kr.co.api.game.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게임 스코어 등록용 내부 DTO
 */
@Getter
@AllArgsConstructor
public class GameScoreRegistrationDto {

    private final Long userId;
    private final Double score;
    private final Long petId;
}
