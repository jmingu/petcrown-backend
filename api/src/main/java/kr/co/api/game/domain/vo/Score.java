package kr.co.api.game.domain.vo;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.Getter;

@Getter
public class Score {

    private final Double value;

    private Score(Double score) {
        validate(score);
        this.value = score;
    }

    public static Score of(Double score) {
        return new Score(score);
    }

    public static Score from(Double score) {
        if (score == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new Score(score);
    }

    private void validate(Double score) {
        if (score == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (score < 0) {
            throw new PetCrownException(BusinessCode.INVALID_REQUEST);
        }
    }
}
