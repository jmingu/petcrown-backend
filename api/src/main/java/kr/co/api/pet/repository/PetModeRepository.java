package kr.co.api.pet.repository;

import kr.co.api.pet.dto.command.PetModeInfoDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.common.jooq.Tables.*;

@Repository
@RequiredArgsConstructor
public class PetModeRepository {

    private final DSLContext dsl;

    /**
     * 펫 감정 모드 전체 목록 조회
     */
    public List<PetModeInfoDto> selectAllPetModes() {
        return dsl.select()
                .from(PET_MODE)
                .where(PET_MODE.DELETE_DATE.isNull())
                .orderBy(PET_MODE.PET_MODE_ID)
                .fetch(this::mapToPetModeInfoDto);
    }

    /**
     * Record를 PetModeInfoDto로 변환
     */
    private PetModeInfoDto mapToPetModeInfoDto(Record record) {
        if (record == null) {
            return null;
        }

        return new PetModeInfoDto(
                record.get(PET_MODE.PET_MODE_ID),
                record.get(PET_MODE.MODE_NAME)
        );
    }
}
