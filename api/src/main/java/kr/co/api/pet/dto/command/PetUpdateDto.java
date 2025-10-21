package kr.co.api.pet.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PetUpdateDto {

    private final Long petId;
    private final Long userId;
    private final Integer breedId;
    private final String customBreed;
    private final String name;
    private final LocalDate birthDate;
    private final String gender;
    private final String description;
    private final String microchipId;
}