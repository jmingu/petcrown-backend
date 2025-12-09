package kr.co.api.pet.domain.model;

import kr.co.api.pet.domain.vo.*;
import kr.co.api.user.domain.model.User;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Pet {

    private final Long petId;
    private final Breed breed;
    private final String customBreed;
    private final Ownership ownership;
    private final User user;
    private final PetName name;
    private final LocalDate birthDate;
    private final PetGender gender;
    private final Weight weight;
    private final Height height;
    private final String isNeutered;
    private final String profileImageUrl;
    private final String microchipId;
    private final String description;


    /**
     * 간단한 펫 등록 (품종 또는 기타품종, 이름)
     */
    public static Pet SetPetObject(Integer breedId, String customBreed, Long userId, String nameValue) {
        // Value Objects 생성 (유효성 검증 포함)
        PetName name = new PetName(nameValue);

        return new Pet(null, Breed.ofId(breedId),
                customBreed, null,
                User.ofId(userId), name, null, null, null, null, null, null, null, null);
    }

    /**
     * 모든 필드로 펫을 생성하는 메서드
     */
    public static Pet getPetAllFiled(Long petId, Breed breed, String customBreed, Ownership ownership, User user,
                                     PetName name, LocalDate birthDate, PetGender gender, Weight weight, Height height,
                                     String isNeutered, String profileImageUrl, String microchipId, String description) {
        return new Pet(petId, breed, customBreed, ownership, user, name, birthDate, gender, weight, height,
                isNeutered, profileImageUrl, microchipId, description);
    }
    
    /**
     * 기존 호환성을 위한 모든 필드 생성 메서드 (String 파라미터)
     */
    public static Pet getPetAllFiledWithStringValues(Long petId, Breed breed, String customBreed, Ownership ownership, User user,
                                     String nameValue, LocalDate birthDate, String genderValue, Double weightValue, Double heightValue,
                                     String isNeutered, String profileImageUrl, String microchipId, String description) {
        
        PetName name = nameValue != null ? new PetName(nameValue) : null;
        PetGender gender = genderValue != null ? new PetGender(genderValue) : null;
        Weight weight = weightValue != null ? new Weight(weightValue) : null;
        Height height = heightValue != null ? new Height(heightValue) : null;
        
        return new Pet(petId, breed, customBreed, ownership, user, name, birthDate, gender, weight, height,
                isNeutered, profileImageUrl, microchipId, description);
    }

    public Long getUserId() {
        return this.user.getUserId();
    }
    
    /**
     * 펫 나이 계산
     */
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }


    
    /**
     * 펫 기본 정보 업데이트 (품종, 이름, 생년월일, 성별, 설명, 마이크로칩 ID)
     */
    public Pet updateBasicInfo(Integer breedId, String customBreed, String nameValue, LocalDate birthDate, 
                              String genderValue, String description, String microchipId) {
        // 유효성 검증
        PetName newName = new PetName(nameValue);
        PetGender newGender = genderValue == null ? null : new PetGender(genderValue);

        if(birthDate != null) {
            // 미래 날짜 검증
            if (birthDate.isAfter(LocalDate.now())) {
                throw new PetCrownException(BusinessCode.INVALID_BIRTH_DATE);
            }
        }

        
        // 새로운 Pet 객체 반환
        return new Pet(
            this.petId,
            breedId != null ? Breed.getBreedAllFiled(breedId, null, null) : this.breed,
            customBreed,
            new Ownership(1),
            this.user,
            newName,
            birthDate,
            newGender,
            this.weight,
            this.height,
            this.isNeutered,
            this.profileImageUrl,
            microchipId,
            description
        );
    }
    
    /**
     * 이름 값 반환 (기존 호환성을 위해)
     */
    public String getNameValue() {
        return name != null ? name.getValue() : null;
    }
    
    /**
     * 성별 값 반환 (기존 호환성을 위해)
     */
    public String getGenderValue() {
        return gender != null ? gender.getCode() : null;
    }
    
    /**
     * 무게 값 반환 (기존 호환성을 위해)
     */
    public Double getWeightValue() {
        return weight != null ? weight.getDoubleValue() : null;
    }
    public BigDecimal getWeight() {
        return weight != null ? weight.getValue() : null;
    }
    
    /**
     * 키 값 반환 (기존 호환성을 위해)
     */
    public Double getHeightValue() {
        return height != null ? height.getDoubleValue() : null;
    }
    public BigDecimal getHeight() {
        return height != null ? height.getValue() : null;
    }

    /**
     * ID만으로 Pet 도메인 생성 (최소 정보)
     */
    public static Pet ofId(Long petId) {
        return new Pet(petId, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
