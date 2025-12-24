package kr.co.api.vote.repository;

import kr.co.api.vote.dto.command.VoteRankInfoDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    public List<VoteRankInfoDto> selectCurrentWeekTopRanking(LocalDate weekStartDate) {

        var vw  = VOTE_WEEKLY.as("vw");
        var p   = PET.as("p");
        var u   = USER.as("u");
        var b   = BREED.as("b");
        var s   = SPECIES.as("s");
        var pm  = PET_MODE.as("pm");
        var vfi = VOTE_FILE_INFO.as("vfi");

        // rank 필드 정의
        Field<Integer> rankField = DSL.rank()
                .over()
                .orderBy(vw.VOTE_COUNT.desc())
                .as("rank");

        // =========================================================
        // 해당 주 데이터만 대상으로 랭킹 계산
        // =========================================================
        Table<?> rankedVw =
                DSL.select(vw.fields())
                        .select(rankField)
                        .from(vw)
                        .where(
                                vw.WEEK_START_DATE.eq(weekStartDate)
                                        .and(vw.DELETE_DATE.isNull())
                        )
                        .asTable("ranked_vw");

        // =========================================================
        // rank <= 3 결과만 JOIN
        // =========================================================
        return dsl
                .select()
                .from(rankedVw)
                .innerJoin(p)
                .on(
                        rankedVw.field(vw.PET_ID).eq(p.PET_ID)
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
                        rankedVw.field(vw.MODE_ID).eq(pm.PET_MODE_ID)
                                .and(pm.DELETE_DATE.isNull())
                )
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(vote_weekly)
                                .and(vfi.REF_ID.eq(rankedVw.field(vw.VOTE_WEEKLY_ID)))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        rankedVw.field("rank", Integer.class).le(3)
                )
                .orderBy(
                        rankedVw.field("rank", Integer.class).asc(),
                        rankedVw.field(vw.VOTE_WEEKLY_ID).asc()
                )
                .fetch(record -> mapToVoteRankInfoDto(record, rankedVw, vw, p, u, b, s, pm, vfi));
    }

    /**
     * 내 주간 랭킹
     */
    public VoteRankInfoDto selectCurrentWeekMyRanking(LocalDate weekStartDate, Long userId) {

        var vw  = VOTE_WEEKLY.as("vw");
        var p   = PET.as("p");
        var u   = USER.as("u");
        var b   = BREED.as("b");
        var s   = SPECIES.as("s");
        var pm  = PET_MODE.as("pm");
        var vfi = VOTE_FILE_INFO.as("vfi");

        // rank 필드 정의
        Field<Integer> rankField = DSL.rank()
                .over()
                .orderBy(vw.VOTE_COUNT.desc())
                .as("rank");

        // =========================================================
        // 해당 주 데이터만 대상으로 랭킹 계산
        // =========================================================
        Table<?> rankedVw =
                DSL.select(vw.fields())
                        .select(rankField)
                        .from(vw)
                        .where(
                                vw.WEEK_START_DATE.eq(weekStartDate)
                                        .and(vw.DELETE_DATE.isNull())
                        )
                        .asTable("ranked_vw");

        // =========================================================
        // rank <= 3 결과만 JOIN
        // =========================================================
        return dsl
                .select()
                .from(rankedVw)
                .innerJoin(p)
                .on(
                        rankedVw.field(vw.PET_ID).eq(p.PET_ID)
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
                        rankedVw.field(vw.MODE_ID).eq(pm.PET_MODE_ID)
                                .and(pm.DELETE_DATE.isNull())
                )
                .leftJoin(vfi)
                .on(
                        vfi.REF_TABLE.eq(vote_weekly)
                                .and(vfi.REF_ID.eq(rankedVw.field(vw.VOTE_WEEKLY_ID)))
                                .and(vfi.DELETE_DATE.isNull())
                )
                .where(
                        u.USER_ID.eq(userId)
                )
                .fetchOne(record -> mapToVoteRankInfoDto(record, rankedVw, vw, p, u, b, s, pm, vfi));
    }

    /**
     * Record를 VoteRankInfoDto로 변환
     */
    private VoteRankInfoDto mapToVoteRankInfoDto(
            Record record,
            Table<?> rankedVw,
            kr.co.common.jooq.tables.VoteWeekly vw,
            kr.co.common.jooq.tables.Pet p,
            kr.co.common.jooq.tables.User u,
            kr.co.common.jooq.tables.Breed b,
            kr.co.common.jooq.tables.Species s,
            kr.co.common.jooq.tables.PetMode pm,
            kr.co.common.jooq.tables.VoteFileInfo vfi
    ) {
        if (record == null) {
            return null;
        }

        Integer voteCount = record.get(rankedVw.field(vw.VOTE_COUNT));

        return new VoteRankInfoDto(
                record.get(rankedVw.field(vw.VOTE_WEEKLY_ID)),
                record.get(p.PET_ID),
                record.get(u.USER_ID),
                record.get(rankedVw.field("rank", Integer.class)),
                record.get(u.NICKNAME),
                record.get(p.NAME),
                record.get(p.GENDER),
                record.get(p.BIRTH_DATE),
                record.get(b.BREED_ID),
                record.get(b.NAME),
                record.get(p.CUSTOM_BREED),
                record.get(s.SPECIES_ID),
                record.get(s.NAME),
                record.get(pm.PET_MODE_ID),
                record.get(pm.MODE_NAME),
                0, // dailyVoteCount
                voteCount != null ? voteCount : 0, // weeklyVoteCount
                0, // monthlyVoteCount
                record.get(rankedVw.field(vw.WEEK_START_DATE)),
                record.get(vfi.FILE_URL),
                record.get(u.EMAIL)
        );
    }
}
