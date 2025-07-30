package kr.co.common.entity.pet;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import kr.co.common.entity.standard.ownership.OwnershipEntity;
import kr.co.common.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Entity
@Table(name = "pet")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // protected 생성자 추가
@Getter
@Slf4j
public class PetEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private BreedEntity breed;

    private String customBreed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownership_id", nullable = false)
    private OwnershipEntity ownership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String name;

    private LocalDate birthDate;

    private String gender;

    private Double weight;

    private Double height;

    private String isNeutered;

    private String profileImageUrl;

    private String microchipId;

    private String description;

    private PetEntity(Long createUserId, Long updateUserId, Long petId, BreedEntity breed, String customBreed, OwnershipEntity ownership, UserEntity user, String name, LocalDate birthDate, String gender, Double weight, Double height, String isNeutered, String profileImageUrl, String microchipId, String description) {

        super(createUserId, updateUserId, "N");
        this.petId = petId;
        this.breed = breed;
        this.customBreed = customBreed;
        this.ownership = ownership;
        this.user = user;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.isNeutered = isNeutered;
        this.profileImageUrl = profileImageUrl;
        this.microchipId = microchipId;
        this.description = description;
    }

    /**
     * PetEntity를 생성하는 메서드
     */
    public static PetEntity createPetEntity(Long petId, BreedEntity breed, String customBreed, OwnershipEntity ownership, UserEntity user, String name, LocalDate birthDate, String gender, Double weight, Double height, String isNeutered, String profileImageUrl, String microchipId, String description) {
        return new PetEntity(
                user.getUserId(),
                user.getUserId(),
                petId,
                breed,
                customBreed,
                ownership,
                user,
                name,
                birthDate,
                gender,
                weight,
                height,
                isNeutered,
                profileImageUrl,
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
}
