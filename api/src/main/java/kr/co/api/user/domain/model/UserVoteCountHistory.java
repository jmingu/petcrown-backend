package kr.co.api.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserVoteCountHistory {

    private final Long voteCountHistoryId;
    private final User user;
    private final Integer changeAmount;
    private final Integer beforeCount;
    private final Integer afterCount;

    /**
     * 기존 데이터로부터 생성
     */
    public static UserVoteCountHistory of(Long voteCountHistoryId, User user, Integer changeAmount, Integer beforeCount, Integer afterCount) {
        return new UserVoteCountHistory(voteCountHistoryId, user, changeAmount, beforeCount, afterCount);
    }

    /**
     * 투표 카운트 이력 생성 (정적 팩토리 메서드)
     */
    public static UserVoteCountHistory create(Long userId, Integer changeAmount, Integer beforeCount, Integer afterCount) {
        return new UserVoteCountHistory(null, User.ofId(userId), changeAmount, beforeCount, afterCount);
    }

}
