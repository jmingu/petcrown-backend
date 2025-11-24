package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 투표 이력 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class VoteHistoryDto {

    private final Long voteHistoryId;
    private final Long userId;
    private final Long voteId;
    private final String email;
    private final String voteCycle;

    /**
     * 등록용 생성자 (voteHistoryId 없음)
     */
    public VoteHistoryDto(Long userId, Long voteId, String email, String voteCycle) {
        this(null, userId, voteId, email, voteCycle);
    }
}
