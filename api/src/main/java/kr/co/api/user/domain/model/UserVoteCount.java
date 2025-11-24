package kr.co.api.user.domain.model;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserVoteCount {

    private final User user;
    private final Integer voteCount;

    private UserVoteCount(User user, Integer voteCount) {
        this.user = user;
        this.voteCount = voteCount;
    }

    /**
     * 회원가입용 투표 카운트 생성 (Factory Method)
     */
    public static UserVoteCount createForRegistration(User user) {
        return new UserVoteCount(user, 1);
    }

    /**
     * 기존 데이터로부터 생성
     */
    public static UserVoteCount of(User user, Integer voteCount) {
        return new UserVoteCount(user, voteCount);
    }


    /**
     * 투표 실행 시 카운트 감소 비즈니스 로직 검증
     */
    public void validateForCastingDecrement() {
        validateCanVote(this.voteCount);
    }


    /**
     * 감소된 카운트 계산
     */
    public Integer calculateDecrementedCount() {
        return Math.max(0, this.voteCount - 1);
    }

    /**
     * 투표 가능 여부 검증 비즈니스 로직
     */
    private static void validateCanVote(Integer currentCount) {
        if (currentCount <= 0) {
            throw new PetCrownException(BusinessCode.VOTE_COUNT_INSUFFICIENT);
        }
    }

}