package kr.co.api.vote.domain.model;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VoteHistory {

    private Long historyId;
    private Long userId;        // 회원 투표 시 사용자 ID
    private String email;       // 비회원 투표 시 이메일
    private Long voteId;        // 투표 ID (Weekly 또는 Monthly의 ID)
    private LocalDate voteDate;
    private String voteCycle;   // "WEEKLY" 또는 "MONTHLY"
    private LocalDateTime createDate;

    /**
     * 회원 투표 이력 생성
     */
    public static VoteHistory createForMember(Long userId, Long voteId, LocalDate voteDate, String voteCycle) {
        validateParameters(userId, null, voteId, voteDate, voteCycle);

        return new VoteHistory(null, userId, null, voteId, voteDate, voteCycle, LocalDateTime.now());
    }

    /**
     * 비회원 투표 이력 생성
     */
    public static VoteHistory createForGuest(String email, Long voteId, LocalDate voteDate, String voteCycle) {
        validateParameters(null, email, voteId, voteDate, voteCycle);

        return new VoteHistory(null, null, email, voteId, voteDate, voteCycle, LocalDateTime.now());
    }

    /**
     * 기존 데이터로부터 생성 (Repository에서 조회 시 사용)
     */
    public static VoteHistory of(Long historyId, Long userId, String email, Long voteId,
                                LocalDate voteDate, String voteCycle, LocalDateTime createDate) {
        return new VoteHistory(historyId, userId, email, voteId, voteDate, voteCycle, createDate);
    }

    /**
     * 회원 투표인지 확인
     */
    public boolean isMemberVote() {
        return userId != null;
    }

    /**
     * 비회원 투표인지 확인
     */
    public boolean isGuestVote() {
        return email != null && userId == null;
    }

    /**
     * 오늘 투표인지 확인
     */
    public boolean isTodayVote() {
        return LocalDate.now().equals(voteDate);
    }

    /**
     * 특정 날짜 투표인지 확인
     */
    public boolean isVoteOnDate(LocalDate targetDate) {
        return targetDate.equals(voteDate);
    }

    /**
     * 파라미터 검증
     */
    private static void validateParameters(Long userId, String email, Long voteId, LocalDate voteDate, String voteCycle) {
        // userId 또는 email 중 하나는 반드시 있어야 함
        if (userId == null && (email == null || email.trim().isEmpty())) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (voteId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (voteDate == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (voteCycle == null || voteCycle.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (!"WEEKLY".equals(voteCycle) && !"MONTHLY".equals(voteCycle)) {
            throw new PetCrownException(BusinessCode.INVALID_REQUEST);
        }
    }
}