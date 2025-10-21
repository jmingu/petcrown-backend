package kr.co.api.pet.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 펫 감정 모드 정보 Command DTO (서비스 레이어 내부용)
 */
@Getter
@AllArgsConstructor
public class PetModeInfoDto {

    private final Integer petModeId;
    private final String modeName;
}
