package kr.co.api.vote.repository;

import kr.co.api.vote.dto.command.VoteInfoDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static kr.co.common.jooq.enums.RefTableEnum.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class VoteRankingRepository {

    private final DSLContext dsl;

    /**
     * 이번 주 Top 3 랭킹 조회 (실시간) - date_trunc 사용
     */
    public List<VoteInfoDto> selectCurrentWeekTopRanking() {
        var vw = VOTE_WEEKLY.as("vw");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");
        var pm = PET_MODE.as("pm");
        var vfi = VOTE_FILE_INFO.as("vfi");

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
                .on(b.BREED_ID.cast(Long.class).eq(p.BREED_ID))
                .leftJoin(s)
                .on(s.SPECIES_ID.cast(Long.class).eq(b.SPECIES_ID))
                .leftJoin(pm)
                .on(
                        vw.MODE_ID.eq(pm.PET_MODE_ID)
                                .and(pm.DELETE_DATE.isNull())
                )
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(vote_weekly)
                                .and(vfi.REF_ID.eq(vw.VOTE_WEEKLY_ID))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        function("date_trunc", Object.class, inline("week"), vw.WEEK_START_DATE)
                                .eq(function("date_trunc", Object.class, inline("week"), currentDate()))
                                .and(vw.DELETE_DATE.isNull())
                )
                .orderBy(
                        vw.VOTE_COUNT.desc(),
                        vw.VOTE_WEEKLY_ID.asc()
                )
                .limit(3)
                .fetch(this::mapToVoteInfoDto);
    }

    /**
     * 지난 주 Top 3 랭킹 조회 - date_trunc 사용
     */
    public List<VoteInfoDto> selectLastWeekTopRanking() {
        var vw = VOTE_WEEKLY.as("vw");
        var p = PET.as("p");
        var u = USER.as("u");
        var b = BREED.as("b");
        var s = SPECIES.as("s");
        var pm = PET_MODE.as("pm");
        var vfi = VOTE_FILE_INFO.as("vfi");

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
                .on(b.BREED_ID.cast(Long.class).eq(p.BREED_ID))
                .leftJoin(s)
                .on(s.SPECIES_ID.cast(Long.class).eq(b.SPECIES_ID))
                .leftJoin(pm)
                .on(
                        vw.MODE_ID.eq(pm.PET_MODE_ID)
                                .and(pm.DELETE_DATE.isNull())
                )
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(vote_weekly)
                                .and(vfi.REF_ID.eq(vw.VOTE_WEEKLY_ID))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        function("date_trunc", Object.class, inline("week"), vw.WEEK_START_DATE)
                                .eq(function("date_trunc", Object.class, inline("week"),
                                        currentDate().minus(7)))  // 7일 전 (지난 주)
                                .and(vw.DELETE_DATE.isNull())
                )
                .orderBy(
                        vw.VOTE_COUNT.desc(),
                        vw.VOTE_WEEKLY_ID.asc()
                )
                .limit(3)
                .fetch(this::mapToVoteInfoDto);
    }

    /**
     * Record를 VoteInfoDto로 변환
     */
    private VoteInfoDto mapToVoteInfoDto(Record record) {
        if (record == null) {
            return null;
        }

        return new VoteInfoDto(
                record.get(VOTE_WEEKLY.VOTE_WEEKLY_ID),
                record.get(PET.PET_ID),
                record.get(USER.USER_ID),
                record.get(PET.NAME),
                record.get(PET.GENDER),
                record.get(PET.BIRTH_DATE),
                record.get(BREED.BREED_ID),
                record.get(BREED.NAME),
                record.get(PET.CUSTOM_BREED),
                record.get(SPECIES.SPECIES_ID),
                record.get(SPECIES.NAME),
                record.get(PET_MODE.PET_MODE_ID),
                record.get(PET_MODE.MODE_NAME),
                0, // dailyVoteCount
                record.get(VOTE_WEEKLY.VOTE_COUNT) != null ? record.get(VOTE_WEEKLY.VOTE_COUNT) : 0, // weeklyVoteCount
                0, // monthlyVoteCount
                record.get(VOTE_WEEKLY.WEEK_START_DATE),
                record.get(VOTE_FILE_INFO.FILE_URL),
                record.get(USER.EMAIL)
        );
    }
}
