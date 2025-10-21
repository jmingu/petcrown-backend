package kr.co.common.entity.pet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // protected 생성자 추가
@Getter
@Slf4j
public class PetEntity {

    private Long petId;

    // BaseEntity 공통 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;

    private Integer breedId;

    private String customBreed;

    private Integer ownershipId;
    private Long userId;

    private String name;

    private LocalDate birthDate;

    private String gender;

    private Double weight;

    private Double height;

    private String isNeutered;

    private String microchipId;

    private String description;

    private PetEntity(Long createUserId, Long updateUserId, Long petId, Integer breedId, String customBreed, Integer ownershipId, Long userId, String name, LocalDate birthDate, String gender, Double weight, Double height, String isNeutered, String microchipId, String description) {
        LocalDateTime now = LocalDateTime.now();
        this.createDate = now;
        this.createUserId = createUserId;
        this.updateDate = now;
        this.updateUserId = updateUserId;
        this.deleteDate = null;
        this.deleteUserId = null;
        this.petId = petId;
        this.breedId = breedId;
        this.customBreed = customBreed;
        this.ownershipId = ownershipId;
        this.userId = userId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.isNeutered = isNeutered;
        this.microchipId = microchipId;
        this.description = description;
    }

    /**
     * PetEntity를 생성하는 메서드
     */
    public static PetEntity createPetEntity(Long petId, Integer breedId, String customBreed, Integer ownershipId, Long userId, String name, LocalDate birthDate, String gender, Double weight, Double height, String isNeutered, String microchipId, String description) {
        return new PetEntity(
                userId,
                userId,
                petId,
                breedId,
                customBreed,
                ownershipId,
                userId,
                name,
                birthDate,
                gender,
                weight,
                height,
                isNeutered,
                microchipId,
                description
        );
    }
    
    /**
     * 펫 정보 업데이트 메서드 (헥사고날 어댑터용)
     */
    public void updatePetInfo(String name, String gender, Double weight, Double height, String description) {
        this.name = name;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.description = description;
    }
    
    /**
     * 펫 업데이트용 Entity 생성
     */
    public static PetEntity createPetEntityForUpdate(Long petId, Integer breedId, String customBreed, Integer ownershipId, Long userId, String name, LocalDate birthDate, String gender, Double weight, Double height, String isNeutered, String microchipId, String description) {
        LocalDateTime now = LocalDateTime.now();
        PetEntity entity = new PetEntity();
        entity.petId = petId;
        entity.breedId = breedId;
        entity.customBreed = customBreed;
        entity.ownershipId = ownershipId;
        entity.userId = userId;
        entity.name = name;
        entity.birthDate = birthDate;
        entity.gender = gender;
        entity.weight = weight;
        entity.height = height;
        entity.isNeutered = isNeutered;
        entity.microchipId = microchipId;
        entity.description = description;
        entity.updateDate = now;
        entity.updateUserId = userId;
        return entity;
    }
}
