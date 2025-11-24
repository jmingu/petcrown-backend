package kr.co.api.vote.repository;

import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.dto.command.VoteFileInfoDto;
import kr.co.api.vote.dto.command.VoteMonthlyDto;
import kr.co.api.vote.dto.command.VoteWeeklyDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class VoteRepository {

    private final DSLContext dsl;

    /**
     * Weekly 투표 등록
     */
    public Long insertVoteWeekly(kr.co.api.vote.domain.VoteWeekly voteWeekly) {
        return dsl.insertInto(VOTE_WEEKLY)
                .set(VOTE_WEEKLY.WEEK_START_DATE, voteWeekly.getWeekStartDate())
                .set(VOTE_WEEKLY.PET_ID, voteWeekly.getPet().getPetId())
                .set(VOTE_WEEKLY.VOTE_COUNT, voteWeekly.getVoteCount())
                .set(VOTE_WEEKLY.VIEW_COUNT, voteWeekly.getViewCount())
                .set(VOTE_WEEKLY.MODE_ID, voteWeekly.getModeId())
                .set(VOTE_WEEKLY.CREATE_DATE, currentLocalDateTime())
                .set(VOTE_WEEKLY.CREATE_USER_ID, voteWeekly.getUser().getUserId())
                .set(VOTE_WEEKLY.UPDATE_DATE, currentLocalDateTime())
                .set(VOTE_WEEKLY.UPDATE_USER_ID, voteWeekly.getUser().getUserId())
                .returningResult(VOTE_WEEKLY.VOTE_WEEKLY_ID)
                .fetchOne(VOTE_WEEKLY.VOTE_WEEKLY_ID);
    }

    /**
     * Monthly 투표 등록
     */
    public Long insertVoteMonthly(VoteMonthlyDto voteMonthlyDto) {
        return dsl.insertInto(VOTE_MONTHLY)
                .set(VOTE_MONTHLY.MONTHLY_START_DATE, voteMonthlyDto.getMonthStartDate())
                .set(VOTE_MONTHLY.PET_ID, voteMonthlyDto.getPetId())
                .set(VOTE_MONTHLY.VOTE_COUNT, voteMonthlyDto.getVoteCount())
                .set(VOTE_MONTHLY.VIEW_COUNT, voteMonthlyDto.getViewCount())
                .set(VOTE_MONTHLY.MODE_ID, voteMonthlyDto.getModeId())
                .set(VOTE_MONTHLY.CREATE_DATE, currentLocalDateTime())
                .set(VOTE_MONTHLY.CREATE_USER_ID, 0L)
                .set(VOTE_MONTHLY.UPDATE_DATE, currentLocalDateTime())
                .set(VOTE_MONTHLY.UPDATE_USER_ID, 0L)
                .returningResult(VOTE_MONTHLY.VOTE_MONTHLY_ID)
                .fetchOne(VOTE_MONTHLY.VOTE_MONTHLY_ID);
    }

    /**
     * 투표 파일 정보 등록
     */
    public Long insertVoteFileInfo(kr.co.api.vote.domain.VoteFileInfo voteFileInfo) {
        return dsl.insertInto(VOTE_FILE_INFO)
                .set(VOTE_FILE_INFO.REF_TABLE, kr.co.common.jooq.enums.RefTableEnum.vote_weekly)
                .set(VOTE_FILE_INFO.REF_ID, voteFileInfo.getVoteWeekly().getVoteWeeklyId())
                .set(VOTE_FILE_INFO.FILE_TYPE, kr.co.common.jooq.enums.FileTypeEnum.IMAGE)
                .set(VOTE_FILE_INFO.SORT_ORDER, 1)
                .set(VOTE_FILE_INFO.FILE_URL, voteFileInfo.getFileUrl())
                .set(VOTE_FILE_INFO.FILE_SIZE, voteFileInfo.getFileSize())
                .set(VOTE_FILE_INFO.MIME_TYPE, voteFileInfo.getMimeType())
                .set(VOTE_FILE_INFO.FILE_NAME, voteFileInfo.getFileName())
                .set(VOTE_FILE_INFO.ORIGINAL_FILE_NAME, voteFileInfo.getOriginalFileName())
                .set(VOTE_FILE_INFO.CREATE_DATE, currentLocalDateTime())
                .set(VOTE_FILE_INFO.CREATE_USER_ID, 0L)
                .set(VOTE_FILE_INFO.UPDATED_DATE, currentLocalDateTime())
                .set(VOTE_FILE_INFO.UPDATE_USER_ID, 0L)
                .returningResult(VOTE_FILE_INFO.VOTE_FILE_ID)
                .fetchOne(VOTE_FILE_INFO.VOTE_FILE_ID);
    }

    /**
     * Weekly 투표 조회 (petId와 weekStartDate로)
     */
    public VoteWeeklyDto selectVoteWeeklyByPetIdAndWeek(Long petId, LocalDate weekStartDate) {
        return dsl.select()
                .from(VOTE_WEEKLY)
                .where(
                        VOTE_WEEKLY.PET_ID.eq(petId)
                                .and(function("date_trunc", LocalDate.class, inline("week"), VOTE_WEEKLY.WEEK_START_DATE)
                                        .eq(function("date_trunc", LocalDate.class, inline("week"), val(weekStartDate))))
                                .and(VOTE_WEEKLY.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToVoteWeeklyDto);
    }

    /**
     * Monthly 투표 조회 (petId와 현재 월) - DB의 date_trunc 사용
     */
    public VoteMonthlyDto selectVoteMonthlyByPetIdAndMonth(Long petId) {
        return dsl.select()
                .from(VOTE_MONTHLY)
                .where(
                        VOTE_MONTHLY.PET_ID.eq(petId)
                                .and(VOTE_MONTHLY.MONTHLY_START_DATE.eq(
                                        function("date_trunc", String.class, inline("month"), currentDate())
                                                .cast(LocalDate.class)
                                ))
                                .and(VOTE_MONTHLY.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToVoteMonthlyDto);
    }

    /**
     * Weekly 투표 목록 조회 (현재 주) - DB의 date_trunc 사용
     */
    public List<VoteInfoDto> selectVoteWeeklyList(long offset, int limit) {
        var vw = VOTE_WEEKLY.as("vw");
        var vfi = VOTE_FILE_INFO.as("vfi");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");
        var pm = PET_MODE.as("pm");

        return dsl.select()
                .from(vw)
                .innerJoin(p)
                .on(
                        vw.PET_ID.eq(p.PET_ID)
                                .and(p.DELETE_DATE.isNull())
                )
                .innerJoin(u)
                .on(
                        p.USER_ID.eq(u.USER_ID)
                                .and(u.DELETE_DATE.isNull())
                )
                .leftJoin(b)
                .on(p.BREED_ID.eq(b.BREED_ID.cast(Long.class)))
                .leftJoin(s)
                .on(b.SPECIES_ID.eq(s.SPECIES_ID.cast(Long.class)))
                .leftJoin(pm)
                .on(
                        vw.MODE_ID.eq(pm.PET_MODE_ID)
                                .and(pm.DELETE_DATE.isNull())
                )
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.vote_weekly)
                                .and(vfi.REF_ID.eq(vw.VOTE_WEEKLY_ID))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        vw.WEEK_START_DATE.eq(
                                function("date_trunc", String.class, inline("week"), currentDate())
                                        .cast(LocalDate.class)
                        )
                                .and(vw.DELETE_DATE.isNull())
                )
                .orderBy(vw.VOTE_WEEKLY_ID.desc())
                .limit(limit)
                .offset((int) offset)
                .fetch(this::mapToVoteInfoDtoForWeekly);
    }

    /**
     * Weekly 투표 목록 개수 - DB의 date_trunc 사용
     */
    public int selectVoteWeeklyListCount() {
        var vw = VOTE_WEEKLY.as("vw");
        var p = PET.as("p");
        var u = USER.as("u");

        Integer count = dsl.selectCount()
                .from(vw)
                .innerJoin(p)
                .on(
                        vw.PET_ID.eq(p.PET_ID)
                                .and(p.DELETE_DATE.isNull())
                )
                .innerJoin(u)
                .on(
                        p.USER_ID.eq(u.USER_ID)
                                .and(u.DELETE_DATE.isNull())
                )
                .where(
                        vw.WEEK_START_DATE.eq(
                                function("date_trunc", String.class, inline("week"), currentDate())
                                        .cast(LocalDate.class)
                        )
                                .and(vw.DELETE_DATE.isNull())
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * Weekly 투표 상세 조회
     */
    public VoteInfoDto selectVoteWeeklyDetail(Long voteId) {
        var vw = VOTE_WEEKLY.as("vw");
        var vfi = VOTE_FILE_INFO.as("vfi");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");
        var pm = PET_MODE.as("pm");

        return dsl.select()
                .from(vw)
                .innerJoin(p)
                .on(
                        vw.PET_ID.eq(p.PET_ID)
                                .and(p.DELETE_DATE.isNull())
                )
                .innerJoin(u)
                .on(
                        p.USER_ID.eq(u.USER_ID)
                                .and(u.DELETE_DATE.isNull())
                )
                .leftJoin(pm)
                .on(
                        vw.MODE_ID.eq(pm.PET_MODE_ID)
                                .and(pm.DELETE_DATE.isNull())
                )
                .leftJoin(b)
                .on(p.BREED_ID.eq(b.BREED_ID.cast(Long.class)))
                .leftJoin(s)
                .on(b.SPECIES_ID.eq(s.SPECIES_ID.cast(Long.class)))
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.vote_weekly)
                                .and(vfi.REF_ID.eq(vw.VOTE_WEEKLY_ID))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        vw.VOTE_WEEKLY_ID.eq(voteId)
                                .and(vw.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToVoteInfoDtoForWeekly);
    }

    /**
     * Monthly 투표 목록 조회 (현재 월)
     */
    public List<VoteInfoDto> selectVoteMonthlyList(LocalDate monthStartDate, long offset, int limit) {
        var vm = VOTE_MONTHLY.as("vm");
        var vfi = VOTE_FILE_INFO.as("vfi");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");

        return dsl.select()
                .from(vm)
                .innerJoin(p)
                .on(
                        vm.PET_ID.eq(p.PET_ID)
                                .and(p.DELETE_DATE.isNull())
                )
                .innerJoin(u)
                .on(
                        p.USER_ID.eq(u.USER_ID)
                                .and(u.DELETE_DATE.isNull())
                )
                .leftJoin(b)
                .on(p.BREED_ID.eq(b.BREED_ID.cast(Long.class)))
                .leftJoin(s)
                .on(b.SPECIES_ID.eq(s.SPECIES_ID.cast(Long.class)))
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.vote_monthly)
                                .and(vfi.REF_ID.eq(vm.VOTE_MONTHLY_ID))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        vm.MONTHLY_START_DATE.eq(monthStartDate)
                                .and(vm.DELETE_DATE.isNull())
                )
                .orderBy(
                        vm.VOTE_COUNT.desc(),
                        vm.VOTE_MONTHLY_ID.desc()
                )
                .limit(limit)
                .offset((int) offset)
                .fetch(this::mapToVoteInfoDtoForMonthly);
    }

    /**
     * Monthly 투표 목록 개수
     */
    public int selectVoteMonthlyListCount(LocalDate monthStartDate) {
        var vm = VOTE_MONTHLY.as("vm");
        var p = PET.as("p");

        Integer count = dsl.selectCount()
                .from(vm)
                .innerJoin(p)
                .on(
                        vm.PET_ID.eq(p.PET_ID)
                                .and(p.DELETE_DATE.isNull())
                )
                .where(
                        vm.MONTHLY_START_DATE.eq(monthStartDate)
                                .and(vm.DELETE_DATE.isNull())
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * Monthly 투표 상세 조회
     */
    public VoteInfoDto selectVoteMonthlyDetail(Long voteMonthlyId) {
        var vm = VOTE_MONTHLY.as("vm");
        var vfi = VOTE_FILE_INFO.as("vfi");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");

        return dsl.select()
                .from(vm)
                .innerJoin(p)
                .on(
                        vm.PET_ID.eq(p.PET_ID)
                                .and(p.DELETE_DATE.isNull())
                )
                .innerJoin(u)
                .on(
                        p.USER_ID.eq(u.USER_ID)
                                .and(u.DELETE_DATE.isNull())
                )
                .leftJoin(b)
                .on(p.BREED_ID.eq(b.BREED_ID.cast(Long.class)))
                .leftJoin(s)
                .on(b.SPECIES_ID.eq(s.SPECIES_ID.cast(Long.class)))
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.vote_monthly)
                                .and(vfi.REF_ID.eq(vm.VOTE_MONTHLY_ID))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        vm.VOTE_MONTHLY_ID.eq(voteMonthlyId)
                                .and(vm.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToVoteInfoDtoForMonthly);
    }

    /**
     * Weekly 투표 카운트 증가
     */
    public void updateVoteWeeklyCount(Long voteWeeklyId, Long updateUserId) {
        dsl.update(VOTE_WEEKLY)
                .set(VOTE_WEEKLY.VOTE_COUNT, VOTE_WEEKLY.VOTE_COUNT.plus(1))
                .set(VOTE_WEEKLY.UPDATE_DATE, currentLocalDateTime())
                .set(VOTE_WEEKLY.UPDATE_USER_ID, updateUserId)
                .where(VOTE_WEEKLY.VOTE_WEEKLY_ID.eq(voteWeeklyId))
                .execute();
    }

    /**
     * Monthly 투표 카운트 증가
     */
    public void updateVoteMonthlyCount(Long voteMonthlyId, Long updateUserId) {
        dsl.update(VOTE_MONTHLY)
                .set(VOTE_MONTHLY.VOTE_COUNT, VOTE_MONTHLY.VOTE_COUNT.plus(1))
                .set(VOTE_MONTHLY.UPDATE_DATE, currentLocalDateTime())
                .set(VOTE_MONTHLY.UPDATE_USER_ID, updateUserId)
                .where(VOTE_MONTHLY.VOTE_MONTHLY_ID.eq(voteMonthlyId))
                .execute();
    }

    /**
     * Weekly 투표 삭제 (논리 삭제)
     */
    public void deleteVoteWeekly(Long voteWeeklyId, Long deleteUserId) {
        dsl.update(VOTE_WEEKLY)
                .set(VOTE_WEEKLY.DELETE_DATE, currentLocalDateTime())
                .set(VOTE_WEEKLY.DELETE_USER_ID, deleteUserId)
                .where(VOTE_WEEKLY.VOTE_WEEKLY_ID.eq(voteWeeklyId))
                .execute();
    }

    /**
     * Monthly 투표 삭제 (논리 삭제)
     */
    public void deleteVoteMonthly(Long voteMonthlyId, Long deleteUserId) {
        dsl.update(VOTE_MONTHLY)
                .set(VOTE_MONTHLY.DELETE_DATE, currentLocalDateTime())
                .set(VOTE_MONTHLY.DELETE_USER_ID, deleteUserId)
                .where(VOTE_MONTHLY.VOTE_MONTHLY_ID.eq(voteMonthlyId))
                .execute();
    }

    /**
     * 투표 파일 정보 수정
     */
    public void updateVoteFileInfo(VoteFileInfoDto voteFileInfoDto) {
        dsl.update(VOTE_FILE_INFO)
                .set(VOTE_FILE_INFO.FILE_URL, voteFileInfoDto.getFileUrl())
                .set(VOTE_FILE_INFO.FILE_SIZE, voteFileInfoDto.getFileSize())
                .set(VOTE_FILE_INFO.MIME_TYPE, voteFileInfoDto.getMimeType())
                .set(VOTE_FILE_INFO.FILE_NAME, voteFileInfoDto.getFileName())
                .set(VOTE_FILE_INFO.ORIGINAL_FILE_NAME, voteFileInfoDto.getOriginalFileName())
                .set(VOTE_FILE_INFO.UPDATED_DATE, currentLocalDateTime())
                .set(VOTE_FILE_INFO.UPDATE_USER_ID, 0L)
                .where(
                        VOTE_FILE_INFO.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.vote_weekly)
                                .and(VOTE_FILE_INFO.REF_ID.eq(voteFileInfoDto.getVoteWeeklyId()))
                                .and(VOTE_FILE_INFO.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 투표 파일 정보 삭제 (논리 삭제)
     */
    public void deleteVoteFileInfo(String refTable, Long refId, Long deleteUserId) {
        dsl.update(VOTE_FILE_INFO)
                .set(VOTE_FILE_INFO.DELETE_DATE, currentLocalDateTime())
                .set(VOTE_FILE_INFO.DELETE_USER_ID, deleteUserId)
                .where(
                        VOTE_FILE_INFO.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.valueOf(refTable))
                                .and(VOTE_FILE_INFO.REF_ID.eq(refId))
                )
                .execute();
    }

    /**
     * 사용자의 현재 주 투표 등록 수 조회 (date_trunc 사용)
     */
    public int countWeeklyVoteRegistrationByUser(Long userId) {
        var vw = VOTE_WEEKLY.as("vw");
        var p = PET.as("p");

        Integer count = dsl.selectCount()
                .from(vw)
                .innerJoin(p)
                .on(vw.PET_ID.eq(p.PET_ID))
                .where(
                        p.USER_ID.eq(userId)
                                .and(function("date_trunc", String.class, inline("week"), vw.WEEK_START_DATE)
                                        .eq(function("date_trunc", String.class, inline("week"), currentDate())))
                                .and(vw.DELETE_DATE.isNull())
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 사용자의 현재 주 투표 등록 수 조회 (date_trunc 사용) - 삭제후 재 등록 방지를 위함
     */
    public int countWeeklyVoteRegistrationByUserNoDelete(Long userId) {
        var vw = VOTE_WEEKLY.as("vw");
        var p = PET.as("p");

        Integer count = dsl.selectCount()
                .from(vw)
                .innerJoin(p)
                .on(vw.PET_ID.eq(p.PET_ID))
                .where(
                        p.USER_ID.eq(userId)
                                .and(function("date_trunc", String.class, inline("week"), vw.WEEK_START_DATE)
                                        .eq(function("date_trunc", String.class, inline("week"), currentDate())))
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * Weekly 투표 수정
     */
    public void updateVoteWeekly(VoteWeeklyDto voteWeeklyDto) {
        dsl.update(VOTE_WEEKLY)
                .set(VOTE_WEEKLY.MODE_ID, voteWeeklyDto.getModeId())
                .set(VOTE_WEEKLY.UPDATE_DATE, currentLocalDateTime())
                .set(VOTE_WEEKLY.UPDATE_USER_ID, voteWeeklyDto.getUserId())
                .where(VOTE_WEEKLY.VOTE_WEEKLY_ID.eq(voteWeeklyDto.getVoteWeeklyId()))
                .execute();
    }

    /**
     * 현재 주의 weekStartDate 조회 (date_trunc 사용)
     */
    public LocalDate selectCurrentWeekStartDate() {
        return dsl.select(
                        function("date_trunc", String.class, inline("week"), currentDate())
                                .cast(LocalDate.class).as("week_start_date")
                )
                .fetchOne(field("week_start_date"), LocalDate.class);
    }

    /**
     * 현재 월의 시작일 조회 (DB의 date_trunc 사용)
     */
    public LocalDate selectCurrentMonthStartDate() {
        return dsl.select(
                        function("date_trunc", String.class, inline("month"), currentDate())
                                .cast(LocalDate.class).as("month_start_date")
                )
                .fetchOne(field("month_start_date"), LocalDate.class);
    }

    /**
     * Record를 VoteWeeklyDto로 변환
     */
    private VoteWeeklyDto mapToVoteWeeklyDto(Record record) {
        if (record == null) {
            return null;
        }

        return new VoteWeeklyDto(
                record.get(VOTE_WEEKLY.VOTE_WEEKLY_ID),
                record.get(VOTE_WEEKLY.PET_ID),
                record.get(VOTE_WEEKLY.CREATE_USER_ID),
                record.get(VOTE_WEEKLY.WEEK_START_DATE),
                record.get(VOTE_WEEKLY.VOTE_COUNT),
                record.get(VOTE_WEEKLY.VIEW_COUNT),
                record.get(VOTE_WEEKLY.MODE_ID)
        );
    }

    /**
     * Record를 VoteMonthlyDto로 변환
     */
    private VoteMonthlyDto mapToVoteMonthlyDto(Record record) {
        if (record == null) {
            return null;
        }

        return new VoteMonthlyDto(
                record.get(VOTE_MONTHLY.VOTE_MONTHLY_ID),
                record.get(VOTE_MONTHLY.MONTHLY_START_DATE),
                record.get(VOTE_MONTHLY.PET_ID),
                record.get(VOTE_MONTHLY.VOTE_COUNT),
                record.get(VOTE_MONTHLY.VIEW_COUNT),
                record.get(VOTE_MONTHLY.MODE_ID)
        );
    }

    /**
     * Record를 VoteInfoDto로 변환 (Weekly용)
     */
    private VoteInfoDto mapToVoteInfoDtoForWeekly(Record record) {
        if (record == null) {
            return null;
        }

        var vw = VOTE_WEEKLY.as("vw");
        var vfi = VOTE_FILE_INFO.as("vfi");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");
        var pm = PET_MODE.as("pm");

        return new VoteInfoDto(
                record.get(vw.VOTE_WEEKLY_ID),
                record.get(p.PET_ID),
                record.get(u.USER_ID),
                record.get(p.NAME),
                record.get(p.GENDER),
                record.get(p.BIRTH_DATE),
                record.get(p.BREED_ID) != null ? record.get(p.BREED_ID).intValue() : null,
                record.get(b.NAME),
                record.get(p.CUSTOM_BREED),
                record.get(b.SPECIES_ID) != null ? record.get(b.SPECIES_ID).intValue() : null,
                record.get(s.NAME),
                record.get(pm.PET_MODE_ID),
                record.get(pm.MODE_NAME),
                0, // dailyVoteCount
                record.get(vw.VOTE_COUNT) != null ? record.get(vw.VOTE_COUNT) : 0, // weeklyVoteCount
                0, // monthlyVoteCount
                record.get(vw.WEEK_START_DATE),
                record.get(vfi.FILE_URL),
                record.get(u.EMAIL)
        );
    }

    /**
     * Record를 VoteInfoDto로 변환 (Monthly용)
     */
    private VoteInfoDto mapToVoteInfoDtoForMonthly(Record record) {
        if (record == null) {
            return null;
        }

        var vm = VOTE_MONTHLY.as("vm");
        var vfi = VOTE_FILE_INFO.as("vfi");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");

        return new VoteInfoDto(
                record.get(vm.VOTE_MONTHLY_ID),
                record.get(p.PET_ID),
                record.get(u.USER_ID),
                record.get(p.NAME),
                record.get(p.GENDER),
                record.get(p.BIRTH_DATE),
                record.get(p.BREED_ID) != null ? record.get(p.BREED_ID).intValue() : null,
                record.get(b.NAME),
                record.get(p.CUSTOM_BREED),
                record.get(b.SPECIES_ID) != null ? record.get(b.SPECIES_ID).intValue() : null,
                record.get(s.NAME),
                0, // petModeId (Monthly에는 없음)
                null, // petModeName (Monthly에는 없음)
                0, // dailyVoteCount
                0, // weeklyVoteCount
                record.get(vm.VOTE_COUNT) != null ? record.get(vm.VOTE_COUNT) : 0, // monthlyVoteCount
                record.get(vm.MONTHLY_START_DATE),
                record.get(vfi.FILE_URL),
                record.get(u.EMAIL)
        );
    }
}
