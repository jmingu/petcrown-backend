package kr.co.api.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 투표 횟수 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class UserVoteCountDto {

    private final Long userId;
    private final Integer voteCount;
    private final LocalDateTime createDate;
    private final Long createUserId;
    private final LocalDateTime updateDate;
    private final Long updateUserId;

    /**
     * 등록용 생성자
     */
    public UserVoteCountDto(Long userId) {
        this(userId, 0, null, userId, null, userId);
    }
}
