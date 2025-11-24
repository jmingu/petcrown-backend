package kr.co.api.vote.domain;

import kr.co.api.pet.domain.Pet;
import kr.co.api.user.domain.model.User;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 주간 투표 도메인 모델
 */
@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class VoteWeekly {

    private final Long voteWeeklyId;
    private final Pet pet;
    private final User user;
    private final LocalDate weekStartDate;
    private final Integer voteCount;
    private final Long viewCount;
    private final Integer modeId;

    /**
     * 주간 투표 생성 (정적 팩토리 메서드)
     */
    public static VoteWeekly createVote(Pet pet, User user, LocalDate weekStartDate, Integer modeId) {
        // 필수값 검증
        if (pet == null || pet.getPetId() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (user == null || user.getUserId() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (weekStartDate == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (modeId == null) {
            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
        }

        return new VoteWeekly(
            null,           // voteWeeklyId (insert 시 null)
            pet,
            user,
            weekStartDate,
            0,              // 초기 투표수
            0L,             // 초기 조회수
            modeId
        );
    }

    /**
     * ID로만 생성 (업데이트용)
     */
    public static VoteWeekly ofId(Long voteWeeklyId) {
        if (voteWeeklyId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new VoteWeekly(voteWeeklyId, null, null, null, null, null, null);
    }

    /**
     * ID가 설정된 새 객체 반환 (불변성 유지)
     */
    public VoteWeekly withId(Long voteWeeklyId) {
        return new VoteWeekly(
            voteWeeklyId,
            this.pet,
            this.user,
            this.weekStartDate,
            this.voteCount,
            this.viewCount,
            this.modeId
        );
    }

}
