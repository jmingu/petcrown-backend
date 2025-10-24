package kr.co.api.pet.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PetRegistrationDto {

    private final Long userId;
    private final Integer breedId;
    private final String customBreed;
    private final String name;
}