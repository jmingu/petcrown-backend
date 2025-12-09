package kr.co.api.pet.domain.model;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Breed {
    private Integer breedId;

    private Species species;

    private String name;

    /**
     *
     */
    public static Breed ofId(Integer breedId) {

        if (breedId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);

        }
        return new Breed(breedId, null, null);
    }


    /**
     * 모든 필드로 생성하는 메서드
     */
    public static Breed getBreedAllFiled(Integer breedId, Species species, String name) {
        return new Breed(breedId, species, name);
    }
}
