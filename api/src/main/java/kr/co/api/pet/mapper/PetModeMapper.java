package kr.co.api.pet.mapper;

import kr.co.api.pet.dto.command.PetModeInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * PetMode Mapper Interface
 */
@Mapper
public interface PetModeMapper {

    /**
     * 펫 감정 모드 전체 목록 조회
     */
    List<PetModeInfoDto> selectAllPetModes();
}
