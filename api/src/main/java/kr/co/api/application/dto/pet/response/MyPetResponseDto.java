package kr.co.api.application.dto.pet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyPetResponseDto {
    private Long petId;
    private String name;
    private String birthDate;
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
    
    // 기본 필드용 간단한 생성자
    public MyPetResponseDto(Long petId, String name, String birthDate, String gender, 
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
