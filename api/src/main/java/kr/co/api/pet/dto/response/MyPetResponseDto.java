package kr.co.api.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class MyPetResponseDto {
    private Long petId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private Double weight;
    private Double height;
    private String imageUrl;
    
    // 확장 필드들 (나중에 필요시 사용)
    private Integer breedId;
    private String breedName;
    private Integer speciesId;
    private String speciesName;
    private Integer awards;
    
    // JPQL 생성자용 (모든 필드 포함)
    public MyPetResponseDto(Long petId, String name, LocalDate birthDate, String gender,
                           Double weight, Double height, String imageUrl,
                           Integer breedId, String breedName, Integer speciesId, 
                           String speciesName, Integer awards) {
        this.petId = petId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.imageUrl = imageUrl;
        this.breedId = breedId;
        this.breedName = breedName;
        this.speciesId = speciesId;
        this.speciesName = speciesName;
        this.awards = awards;
    }
    
    // 기본 필드용 간단한 생성자
    public MyPetResponseDto(Long petId, String name, LocalDate birthDate, String gender,
                           Double weight, Double height, String imageUrl) {
        this.petId = petId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.imageUrl = imageUrl;
    }
}