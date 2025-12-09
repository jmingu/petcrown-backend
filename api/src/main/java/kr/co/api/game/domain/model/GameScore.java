package kr.co.api.game.domain.model;

import kr.co.api.game.domain.vo.Score;
import kr.co.api.pet.domain.model.Pet;
import kr.co.api.user.domain.model.User;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GameScore {

    private final Long scoreId;
    private final User user;
    private final LocalDate weekStartDate;
    private final Score score;
    private final Pet pet;

    /**
     * 게임 스코어 등록 (새로운 스코어 생성)
     */
    public static GameScore create(Long userId, Double scoreValue, Long petId) {
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        Score score = Score.of(scoreValue);

        // 현재 주의 시작일 계산 (월요일 기준)
        LocalDate weekStart = calculateWeekStartDate(LocalDate.now());

        return new GameScore(null, User.ofId(userId), weekStart, score, Pet.ofId(petId));
    }

    /**
     * 기존 스코어 조회용 (Repository에서 사용)
     */
//    public static GameScore of(Long scoreId, Long userId, LocalDate weekStartDate, Double scoreValue, String imageUrl) {
//        if (scoreId == null || userId == null || weekStartDate == null) {
//            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
//        }
//
//        Score score = Score.from(scoreValue);
//
//        return new GameScore(scoreId, User.ofId(userId), weekStartDate, score, imageUrl);
//    }

    /**
     * 스코어 업데이트용 (scoreId, userId, weekStartDate, score, imageUrl 모두 필요)
     */
    public static GameScore forUpdate(Long scoreId, Long userId, LocalDate weekStartDate, Double scoreValue, Long petId) {
        if (scoreId == null || userId == null || weekStartDate == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        Score score = Score.of(scoreValue);

        return new GameScore(scoreId, User.ofId(userId), weekStartDate, score, Pet.ofId(petId));
    }

    /**
     * 주의 시작일 계산 (월요일 기준)
     */
    private static LocalDate calculateWeekStartDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int daysToSubtract = dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue();
        return date.minusDays(daysToSubtract);
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public Long getPetId() {
        return pet.getPetId();
    }
}
