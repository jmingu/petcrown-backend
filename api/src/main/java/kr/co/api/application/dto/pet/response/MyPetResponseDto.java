package kr.co.api.application.dto.pet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyPetResponseDto {
    private Long petId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private Integer breedId;
    private String breedName;
    private Integer speciesId;
    private String speciesName;
    private String imageUrl;
    private Integer awards;

}
