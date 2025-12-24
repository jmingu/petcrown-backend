package kr.co.api.game.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 주간 랭킹 조회용 내부 DTO
 */
@Getter
@AllArgsConstructor
public class WeeklyRankingDto {

    private final Long scoreId;
    private final Integer ranking;
    private final Long userId;
    private final String nickname;
    private final Double score;
    private final Long petId;
    private final String name; // 펫이름
    private final String imageUrl;
}
