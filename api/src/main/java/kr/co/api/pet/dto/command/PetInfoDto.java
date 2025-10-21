package kr.co.api.pet.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PetInfoDto {

    private final Long petId;
    private final Long userId;
    private final String name;
    private final Integer breedId;
    private final String breedName;
    private final Integer speciesId;
    private final String speciesName;
    private final String customBreed;
    private final String gender;
    private final LocalDate birthDate;
    private final String description;
    private final String microchipId;
    private final Integer ownershipId;
    private final String ownershipName;
    private final String imageUrl;
    private final LocalDate createDate;
}