package kr.co.api.pet.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 종 정보 Command DTO (서비스 레이어 내부용)
 */
@Getter
@AllArgsConstructor
public class SpeciesInfoDto {

    private final Long speciesId;
    private final String name;
}
