package kr.co.api.pet.converter.entityCommand;

import kr.co.api.pet.dto.command.PetInfoDto;
import kr.co.common.entity.pet.PetEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * PetEntity를 Command DTO로 변환하는 Converter
 * Infrastructure Layer와 Application Service Layer 간의 변환을 담당
 */
@Component
public class PetEntityCommandConverter {

    /**
     * PetEntity를 PetInfoDto로 변환
     *
     * @param petEntity Infrastructure Layer의 Entity
     * @param breedName 품종명 (조인으로 가져온 데이터)
     * @param speciesId 종 ID (조인으로 가져온 데이터)
     * @param speciesName 종명 (조인으로 가져온 데이터)
     * @param ownershipName 소유권명 (조인으로 가져온 데이터)
     * @return Application Service Layer의 CommandDto
     */
    public PetInfoDto toPetInfoDto(PetEntity petEntity, String breedName, Integer speciesId,
                                  String speciesName, String ownershipName) {
        if (petEntity == null) {
            return null;
        }

        // createDate는 LocalDateTime을 LocalDate로 변환
        LocalDate createDate = petEntity.getCreateDate() != null
            ? petEntity.getCreateDate().toLocalDate()
            : null;

        return new PetInfoDto(
                petEntity.getPetId(),
                petEntity.getUserId(),
                petEntity.getName(),
                petEntity.getBreedId(),
                breedName,
                speciesId,
                speciesName,
                petEntity.getCustomBreed(),
                petEntity.getGender(),
                petEntity.getBirthDate(),
                petEntity.getDescription(),
                petEntity.getMicrochipId(),
                petEntity.getOwnershipId(),
                ownershipName,
                null,  // imageUrl는 MyBatis 쿼리에서 file_info 테이블 조인으로 가져옴
                createDate
        );
    }

    /**
     * PetEntity를 PetInfoDto로 변환 (단순 버전)
     * 조인된 데이터 없이 기본 정보만 변환
     *
     * @param petEntity Infrastructure Layer의 Entity
     * @return Application Service Layer의 CommandDto
     */
    public PetInfoDto toPetInfoDto(PetEntity petEntity) {
        return toPetInfoDto(petEntity, null, null, null, null);
    }
}