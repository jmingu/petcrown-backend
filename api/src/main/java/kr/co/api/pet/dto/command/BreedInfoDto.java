package kr.co.api.pet.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 품종 정보 Command DTO (서비스 레이어 내부용)
 */
@Getter
@AllArgsConstructor
public class BreedInfoDto {

    private final Long breedId;
    private final String name;
}
