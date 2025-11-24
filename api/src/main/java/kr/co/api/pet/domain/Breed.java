package kr.co.api.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Breed {
    private Integer breedId;

    private Species species;

    private String name;



    /**
     * 모든 필드로 생성하는 메서드
     */
    public static Breed getBreedAllFiled(Integer breedId, Species species, String name) {
        return new Breed(breedId, species, name);
    }
}
