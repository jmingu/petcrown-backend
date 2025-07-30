package kr.co.api.domain.model.pet;

import kr.co.api.domain.model.pet.vo.*;
import kr.co.api.domain.model.standard.ownership.Ownership;
import kr.co.api.domain.model.user.User;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.DateUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Pet {

    private Long petId;
    private Breed breed;
    private String customBreed;
    private Ownership ownership;
    private User user;
    private PetName name;
    private LocalDate birthDate;
    private PetGender gender;
    private Weight weight;
    private Height height;
    private String isNeutered;
    private String profileImageUrl;
    private String microchipId;
    private String description;


    /**
     * 펫을 생성하는 메서드
     */
    public static Pet SetPetObject(Integer breedId, String customBreed, Integer ownershipId, Long userId, String nameValue, String birthDate,
                              String genderValue, String microchipId, String description) {

        // Value Objects 생성 (유효성 검증 포함)
        PetName name = new PetName(nameValue);
        PetGender gender = new PetGender(genderValue);
        
        // 생년월일 변환 및 검증
        if (birthDate == null || birthDate.length() != 8) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");
        
        // 미래 날짜 검증
        if (localDate.isAfter(LocalDate.now())) {
            throw new PetCrownException(BusinessCode.INVALID_BIRTH_DATE);
        }

        return new Pet(null, Breed.getBreedAllFiled(breedId, null, null), customBreed, new Ownership(ownershipId), 
                User.createUserById(userId), name, localDate, gender, null, null, null, null, microchipId, description);
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
     * 강아지 여부 확인 (1살 미만)
     */
    public boolean isPuppy() {
        return getAge() < 1;
    }
    
    /**
     * 시니어 여부 확인 (7살 이상)
     */
    public boolean isSenior() {
        return getAge() >= 7;
    }
    
    /**
     * 중성화 여부 확인
     */
    public boolean isNeutered() {
        return "Y".equals(isNeutered);
    }
    
    /**
     * 펫 정보 업데이트
     */
    public void updatePetInfo(PetName name, PetGender gender, Weight weight, Height height, String description) {
        this.name = name;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.description = description;
    }
    
    /**
     * 프로필 이미지 업데이트
     */
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    /**
     * 중성화 상태 업데이트
     */
    public void updateNeuteredStatus(boolean isNeutered) {
        this.isNeutered = isNeutered ? "Y" : "N";
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
    
    /**
     * 키 값 반환 (기존 호환성을 위해)
     */
    public Double getHeightValue() {
        return height != null ? height.getDoubleValue() : null;
    }
}
