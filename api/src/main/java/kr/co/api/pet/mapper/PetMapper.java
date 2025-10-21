package kr.co.api.pet.mapper;

import kr.co.api.pet.dto.command.PetInfoDto;
import kr.co.api.pet.dto.command.PetRegistrationDto;
import kr.co.api.pet.dto.command.PetUpdateDto;
import kr.co.api.pet.dto.command.SpeciesInfoDto;
import kr.co.api.pet.dto.command.BreedInfoDto;
import kr.co.common.entity.pet.PetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Pet Mapper Interface
 */
@Mapper
public interface PetMapper {

    /**
     * 펫 등록
     */
    void insertPet(@Param("dto") PetRegistrationDto petRegistrationDto, @Param("imageUrl") String imageUrl);

    /**
     * 펫 등록 (Entity 기반)
     */
    void insertPetEntity(@Param("entity") PetEntity petEntity);

    /**
     * 사용자별 펫 목록 조회
     */
    List<PetInfoDto> selectPetListByUserId(@Param("userId") Long userId);

    /**
     * 펫 단일 조회
     */
    PetInfoDto selectPetById(@Param("petId") Long petId);

    /**
     * 펫 정보 수정
     */
    void updatePet(@Param("dto") PetUpdateDto petUpdateDto);

    /**
     * 펫 정보 수정 (Entity 기반)
     */
    void updatePetEntity(@Param("entity") PetEntity petEntity);

    /**
     * 펫 삭제 (논리삭제)
     */
    void deletePet(@Param("petId") Long petId, @Param("userId") Long userId);

    /**
     * 종 목록 조회 (species_id != 0)
     */
    List<SpeciesInfoDto> selectAllSpecies();

    /**
     * 품종 목록 조회 (특정 종)
     */
    List<BreedInfoDto> selectBreedsBySpeciesId(@Param("speciesId") Long speciesId);
}