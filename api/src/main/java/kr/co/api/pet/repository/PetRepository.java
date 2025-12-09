package kr.co.api.pet.repository;

import kr.co.api.pet.domain.model.Pet;
import kr.co.api.pet.dto.command.BreedInfoDto;
import kr.co.api.pet.dto.command.PetInfoDto;
import kr.co.api.pet.dto.command.PetRegistrationDto;
import kr.co.api.pet.dto.command.PetUpdateDto;
import kr.co.api.pet.dto.command.SpeciesInfoDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static kr.co.common.jooq.enums.RefTableEnum.pet;
import static kr.co.common.jooq.enums.FileTypeEnum.IMAGE;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class PetRepository {

    private final DSLContext dsl;

    /**
     * 펫 등록 (DTO 기반) - 생성된 petId 반환
     */
    public Long insertPet(PetRegistrationDto petRegistrationDto, String imageUrl) {
        return dsl.insertInto(PET)
                .set(PET.BREED_ID, petRegistrationDto.getBreedId() != null ? petRegistrationDto.getBreedId().longValue() : null)
                .set(PET.CUSTOM_BREED, petRegistrationDto.getCustomBreed())
                .set(PET.USER_ID, petRegistrationDto.getUserId())
                .set(PET.NAME, petRegistrationDto.getName())
                .set(PET.CREATE_DATE, currentLocalDateTime())
                .set(PET.CREATE_USER_ID, petRegistrationDto.getUserId())
                .set(PET.UPDATE_DATE, currentLocalDateTime())
                .set(PET.UPDATE_USER_ID, petRegistrationDto.getUserId())
                .returningResult(PET.PET_ID)
                .fetchOne()
                .getValue(PET.PET_ID);
    }

    /**
     * 펫 등록 (Entity 기반) - 생성된 petId 반환
     */
    public Long insertPetEntity(Pet pet) {
        return dsl.insertInto(PET)
                .set(PET.BREED_ID, DSL.val(
                        pet.getBreed() != null ? pet.getBreed().getBreedId() : null, Long.class
                ))
                .set(PET.CUSTOM_BREED, pet.getCustomBreed())
                .set(PET.OWNERSHIP_ID, DSL.val(
                        pet.getOwnership() != null ? pet.getOwnership().getOwnershipId() : null, Long.class
                ))
                .set(PET.USER_ID, pet.getUserId())
                .set(PET.NAME, pet.getName().getValue())
                .set(PET.BIRTH_DATE, pet.getBirthDate())
                .set(PET.GENDER, pet.getGender() != null ? pet.getGender().getCode() : null)
                .set(PET.WEIGHT, pet.getWeight())
                .set(PET.HEIGHT, pet.getHeight())
                .set(PET.IS_NEUTERED, pet.getIsNeutered())
                .set(PET.MICROCHIP_ID, pet.getMicrochipId())
                .set(PET.DESCRIPTION, pet.getDescription())
                .set(PET.CREATE_DATE, currentLocalDateTime())
                .set(PET.CREATE_USER_ID, pet.getUserId())
                .set(PET.UPDATE_DATE, currentLocalDateTime())
                .set(PET.UPDATE_USER_ID, pet.getUserId())
                .returningResult(PET.PET_ID)
                .fetchOne()
                .getValue(PET.PET_ID);
    }

    /**
     * 사용자별 펫 목록 조회
     */
    public List<PetInfoDto> selectPetListByUserId(Long userId) {
        var p = PET.as("p");
        var b = BREED.as("b");
        var s = SPECIES.as("s");
        var o = OWNERSHIP.as("o");
        var f = FILE_INFO.as("f");

        return dsl.select(
                        p.PET_ID,
                        p.USER_ID,
                        p.NAME,
                        b.BREED_ID,
                        b.NAME.as("breed_name"),
                        s.SPECIES_ID,
                        s.NAME.as("species_name"),
                        p.CUSTOM_BREED,
                        p.GENDER,
                        p.BIRTH_DATE,
                        p.DESCRIPTION,
                        p.MICROCHIP_ID,
                        o.OWNERSHIP_ID,
                        o.OWNERSHIP_NAME.as("ownership_name"),
                        f.FILE_URL,
                        p.CREATE_DATE
                )
                .from(p)
                .leftJoin(b).on(b.BREED_ID.cast(Long.class).eq(p.BREED_ID))
                .leftJoin(s).on(s.SPECIES_ID.cast(Long.class).eq(b.SPECIES_ID))
                .leftJoin(o).on(o.OWNERSHIP_ID.cast(Long.class).eq(p.OWNERSHIP_ID))
                .leftJoin(f).on(
                        f.REF_TABLE.eq(pet)
                                .and(f.REF_ID.eq(p.PET_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(
                        p.USER_ID.eq(userId)
                                .and(p.DELETE_DATE.isNull())
                )
                .orderBy(p.CREATE_DATE.desc())
                .fetch(this::mapToPetInfoDto);
    }

    /**
     * 펫 단일 조회
     */
    public PetInfoDto selectPetById(Long petId) {
        var p = PET.as("p");
        var b = BREED.as("b");
        var s = SPECIES.as("s");
        var o = OWNERSHIP.as("o");
        var f = FILE_INFO.as("f");

        return dsl.select(
                        p.PET_ID,
                        p.USER_ID,
                        p.NAME,
                        b.BREED_ID,
                        b.NAME.as("breed_name"),
                        s.SPECIES_ID,
                        s.NAME.as("species_name"),
                        p.CUSTOM_BREED,
                        p.GENDER,
                        p.BIRTH_DATE,
                        p.DESCRIPTION,
                        p.MICROCHIP_ID,
                        o.OWNERSHIP_ID,
                        o.OWNERSHIP_NAME.as("ownership_name"),
                        f.FILE_URL,
                        p.CREATE_DATE
                )
                .from(p)
                .leftJoin(b).on(b.BREED_ID.cast(Long.class).eq(p.BREED_ID))
                .leftJoin(s).on(s.SPECIES_ID.cast(Long.class).eq(b.SPECIES_ID))
                .leftJoin(o).on(o.OWNERSHIP_ID.cast(Long.class).eq(p.OWNERSHIP_ID))
                .leftJoin(f).on(
                        f.REF_TABLE.eq(pet)
                                .and(f.REF_ID.eq(p.PET_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(
                        p.PET_ID.eq(petId)
                                .and(p.DELETE_DATE.isNull())
                )
                .orderBy(p.CREATE_DATE.desc())
                .fetchOne(this::mapToPetInfoDto);
    }

    /**
     * 펫 정보 수정 (DTO 기반)
     */
    public void updatePet(PetUpdateDto petUpdateDto) {
        dsl.update(PET)
                .set(PET.BREED_ID, petUpdateDto.getBreedId() != null ? petUpdateDto.getBreedId().longValue() : null)
                .set(PET.CUSTOM_BREED, petUpdateDto.getCustomBreed())
                .set(PET.NAME, petUpdateDto.getName())
                .set(PET.BIRTH_DATE, petUpdateDto.getBirthDate())
                .set(PET.GENDER, petUpdateDto.getGender())
                .set(PET.DESCRIPTION, petUpdateDto.getDescription())
                .set(PET.MICROCHIP_ID, petUpdateDto.getMicrochipId())
                .set(PET.UPDATE_DATE, currentLocalDateTime())
                .set(PET.UPDATE_USER_ID, petUpdateDto.getUserId())
                .where(
                        PET.PET_ID.eq(petUpdateDto.getPetId())
                                .and(PET.USER_ID.eq(petUpdateDto.getUserId()))
                                .and(PET.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 펫 정보 수정 (Entity 기반)
     */
    public void updatePetEntity(Pet pet) {
        dsl.update(PET)
                .set(PET.BREED_ID, DSL.val(
                        pet.getBreed() != null ? pet.getBreed().getBreedId() : null, Long.class
                ))
                .set(PET.CUSTOM_BREED, pet.getCustomBreed())
                .set(PET.OWNERSHIP_ID, DSL.val(
                        pet.getOwnership() != null ? pet.getOwnership().getOwnershipId(): null, Long.class
                ))
                .set(PET.NAME, pet.getName().getValue())
                .set(PET.BIRTH_DATE, pet.getBirthDate())
                .set(PET.GENDER, pet.getGender() != null ? pet.getGender().getCode() : null)
                .set(PET.WEIGHT, pet.getWeight())
                .set(PET.HEIGHT, pet.getHeight())
                .set(PET.IS_NEUTERED, pet.getIsNeutered())
                .set(PET.MICROCHIP_ID, pet.getMicrochipId())
                .set(PET.DESCRIPTION, pet.getDescription())
                .set(PET.UPDATE_DATE, currentLocalDateTime())
                .set(PET.UPDATE_USER_ID, pet.getUserId())
                .where(
                        PET.PET_ID.eq(pet.getPetId())
                                .and(PET.USER_ID.eq(pet.getUserId()))
                                .and(PET.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 펫 삭제 (논리삭제)
     */
    public void deletePet(Long petId, Long userId) {
        dsl.update(PET)
                .set(PET.DELETE_USER_ID, userId)
                .set(PET.DELETE_DATE, currentLocalDateTime())
                .set(PET.UPDATE_USER_ID, userId)
                .where(PET.PET_ID.eq(petId))
                .execute();
    }

    /**
     * 사용자의 모든 펫 삭제 (논리삭제)
     */
    public void deleteAllPetsByUserId(Long userId, Long deleteUserId) {
        dsl.update(PET)
                .set(PET.DELETE_USER_ID, deleteUserId)
                .set(PET.DELETE_DATE, currentLocalDateTime())
                .set(PET.UPDATE_USER_ID, deleteUserId)
                .where(PET.USER_ID.eq(userId))
                .execute();
    }

    /**
     * 종 목록 조회 (species_id != 0)
     */
    public List<SpeciesInfoDto> selectAllSpecies() {
        return dsl.select(
                        SPECIES.SPECIES_ID,
                        SPECIES.NAME
                )
                .from(SPECIES)
                .orderBy(SPECIES.SPECIES_ID)
                .fetch(this::mapToSpeciesInfoDto);
    }

    /**
     * 품종 목록 조회 (특정 종)
     */
    public List<BreedInfoDto> selectBreedsBySpeciesId(Long speciesId) {
        return dsl.select(
                        BREED.BREED_ID,
                        BREED.NAME
                )
                .from(BREED)
                .where(BREED.SPECIES_ID.eq(speciesId))
                .orderBy(BREED.BREED_ID)
                .fetch(this::mapToBreedInfoDto);
    }

    /**
     * Record를 PetInfoDto로 변환
     */
    private PetInfoDto mapToPetInfoDto(Record record) {
        if (record == null) {
            return null;
        }

        return new PetInfoDto(
                record.get("pet_id", Long.class),
                record.get("user_id", Long.class),
                record.get("name", String.class),
                record.get("breed_id", Integer.class),
                record.get("breed_name", String.class),
                record.get("species_id", Integer.class),
                record.get("species_name", String.class),
                record.get("custom_breed", String.class),
                record.get("gender", String.class),
                record.get("birth_date", LocalDate.class),
                record.get("description", String.class),
                record.get("microchip_id", String.class),
                record.get("ownership_id", Integer.class),
                record.get("ownership_name", String.class),
                record.get("file_url", String.class),
                record.get("create_date", LocalDate.class)
        );
    }

    /**
     * Record를 SpeciesInfoDto로 변환
     */
    private SpeciesInfoDto mapToSpeciesInfoDto(Record record) {
        if (record == null) {
            return null;
        }

        return new SpeciesInfoDto(
                record.get("species_id", Long.class),
                record.get("name", String.class)
        );
    }

    /**
     * Record를 BreedInfoDto로 변환
     */
    private BreedInfoDto mapToBreedInfoDto(Record record) {
        if (record == null) {
            return null;
        }

        return new BreedInfoDto(
                record.get("breed_id", Long.class),
                record.get("name", String.class)
        );
    }
}
