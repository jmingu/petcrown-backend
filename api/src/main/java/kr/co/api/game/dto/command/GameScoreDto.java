package kr.co.api.game.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 게임 스코어 조회용 내부 DTO
 */
@Getter
@AllArgsConstructor
public class GameScoreDto {

    private final Long scoreId;
    private final Long userId;
    private final LocalDate weekStartDate;
    private final Double score;
    private final Long petId;
    private final String nickname;
    private final String name;
    private final String imageUrl;
}
