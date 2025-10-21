package kr.co.api.user.domain.model;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserVoteCount {

    private final Long userId;
    private final Integer voteCount;
    private final LocalDateTime createDate;
    private final Long createUserId;
    private final LocalDateTime updateDate;
    private final Long updateUserId;

    private UserVoteCount(Long userId, Integer voteCount, LocalDateTime createDate, Long createUserId,
                         LocalDateTime updateDate, Long updateUserId) {
        this.userId = userId;
        this.voteCount = voteCount;
        this.createDate = createDate;
        this.createUserId = createUserId;
        this.updateDate = updateDate;
        this.updateUserId = updateUserId;
    }

    /**
     * 회원가입용 투표 카운트 생성 (Factory Method)
     */
    public static UserVoteCount createForRegistration(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return new UserVoteCount(userId, 1, now, userId, now, userId);
    }

    /**
     * 기존 데이터로부터 생성
     */
    public static UserVoteCount of(Long userId, Integer voteCount, LocalDateTime createDate, Long createUserId,
                                  LocalDateTime updateDate, Long updateUserId) {
        return new UserVoteCount(userId, voteCount, createDate, createUserId, updateDate, updateUserId);
    }

    /**
     * 투표 등록 시 카운트 증가 비즈니스 로직 검증
     */
    public void validateForRegistrationIncrement() {
        validateVoteCountOperation(this.voteCount, "increment");
    }

    /**
     * 투표 실행 시 카운트 감소 비즈니스 로직 검증
     */
    public void validateForCastingDecrement() {
        validateCanVote(this.voteCount);
    }

    /**
     * 증가된 카운트 계산
     */
    public Integer calculateIncrementedCount() {
        return this.voteCount + 1;
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

    /**
     * 투표 카운트 연산 유효성 검증
     */
    private static void validateVoteCountOperation(Integer currentCount, String operation) {
        if (currentCount < 0) {
            throw new PetCrownException(BusinessCode.INVALID_VOTE_COUNT_STATE);
        }

        if ("increment".equals(operation) && currentCount >= 999) {
            throw new PetCrownException(BusinessCode.VOTE_COUNT_LIMIT_EXCEEDED);
        }
    }
}