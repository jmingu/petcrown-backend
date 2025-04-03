package kr.co.common.entity.pet;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import kr.co.common.entity.breed.BreedEntity;
import kr.co.common.entity.standard.ownership.OwnershipEntity;
import kr.co.common.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Entity
@Table(name = "pet")
@AllArgsConstructor
@NoArgsConstructor
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

    public PetEntity(Long createUserId, Long updateUserId, Long petId, BreedEntity breed, String customBreed, OwnershipEntity ownership, UserEntity user, String name, LocalDate birthDate, String gender, Double weight, Double height, String isNeutered, String profileImageUrl, String microchipId, String description) {

        super(createUserId, updateUserId);
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
}
