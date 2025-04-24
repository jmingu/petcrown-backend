package kr.co.api.domain.model.pet;

import kr.co.api.domain.model.breed.Breed;
import kr.co.api.domain.model.standard.ownership.Ownership;
import kr.co.api.domain.model.user.Password;
import kr.co.api.domain.model.user.User;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.DateUtils;
import kr.co.common.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Pet {

    private Long petId;
    private Breed breed;
    private String customBreed;
    private Ownership ownership;
    private User user;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private Double weight;
    private Double height;
    private String isNeutered;
    private String profileImageUrl;
    private String microchipId;
    private String description;

    public static Pet savePetObject(Integer breedId, String customBreed, Integer ownershipId, Long userId, String name, String birthDate,
                              String gender,String profileImageUrl, String microchipId, String description) {

        ValidationUtils.validateString(name, 1, 20);
        ValidationUtils.validateString(birthDate, 8, 8);

            if (!"M".equals(gender) && !"F".equals(gender)) {
                throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
            }


        // 생년월인 변환
        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");


        return new Pet(null, new Breed(breedId), customBreed, new Ownership(ownershipId), new User(userId), name, localDate, gender, null, null,
                null, profileImageUrl, microchipId, description);
    }

    public Integer getBreedId() {
        return this.breed.getBreedId();
    }
    public Integer getOwnershipId() {
        return this.ownership.getOwnershipId();
    }
    public Long getUserId() {
        return this.user.getUserId();
    }
}
