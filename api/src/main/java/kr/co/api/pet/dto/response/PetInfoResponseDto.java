package kr.co.api.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetInfoResponseDto {
    
    private Long petId;
    private String name;
    private Integer breedId;
    private String breedName;
    private Integer speciesId;
    private String speciesName;
    private String customBreed;
    private String gender;
    private LocalDate birthDate;
    private String description;
    private String microchipId;
    private Integer ownershipId;
    private String ownershipName;
    private String imageUrl;
    private LocalDate createDate;
}