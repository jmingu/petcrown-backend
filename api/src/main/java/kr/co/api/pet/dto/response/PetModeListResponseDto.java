package kr.co.api.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 펫 감정 모드 목록 Response DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetModeListResponseDto {

    private List<PetModeDto> petModes;
}
