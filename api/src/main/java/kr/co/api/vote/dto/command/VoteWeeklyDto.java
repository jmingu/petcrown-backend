package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 주간 투표 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class VoteWeeklyDto {

    private final Long voteWeeklyId;
    private final Long petId;
    private final Long userId;
    private final LocalDate weekStartDate;
    private final Integer voteCount;
    private final Long viewCount;
    private final Integer modeId;

    /**
     * 등록용 생성자 (voteWeeklyId 없음)
     */
    public VoteWeeklyDto(Long petId, Long userId, LocalDate weekStartDate, Integer voteCount,
                        Long viewCount, Integer modeId) {
        this(null, petId, userId, weekStartDate, voteCount, viewCount, modeId);
    }
}
