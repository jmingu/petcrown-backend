package kr.co.api.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 펫 감정 모드 Response DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetModeDto {

    private Integer petModeId;
    private String modeName;
}
