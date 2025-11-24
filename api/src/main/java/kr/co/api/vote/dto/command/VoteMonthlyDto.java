package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 월간 투표 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class VoteMonthlyDto {

    private final Long voteMonthlyId;
    private final LocalDate monthStartDate;
    private final Long petId;
    private final Integer voteCount;
    private final Long viewCount;
    private final Integer modeId;

    /**
     * 등록용 생성자 (voteMonthlyId 없음)
     */
    public VoteMonthlyDto(LocalDate monthStartDate, Long petId, Integer voteCount,
                         Long viewCount, Integer modeId) {
        this(null, monthStartDate, petId, voteCount, viewCount, modeId);
    }
}
