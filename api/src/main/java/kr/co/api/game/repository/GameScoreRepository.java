package kr.co.api.game.repository;

import kr.co.api.game.domain.model.GameScore;
import kr.co.api.game.dto.command.GameScoreDto;
import kr.co.api.game.dto.command.WeeklyRankingDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static kr.co.common.jooq.Tables.*;
import static kr.co.common.jooq.enums.FileTypeEnum.IMAGE;
import static kr.co.common.jooq.enums.RefTableEnum.pet;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class GameScoreRepository {

    private final DSLContext dsl;


    public void save(GameScore gameScore) {
        dsl.insertInto(GAME_SCORE)
                .set(GAME_SCORE.USER_ID, gameScore.getUserId())
                .set(GAME_SCORE.WEEK_START_DATE, gameScore.getWeekStartDate())
                .set(GAME_SCORE.SCORE, gameScore.getScore().getValue())
                .set(GAME_SCORE.PET_ID, gameScore.getPetId())
                .set(GAME_SCORE.CREATE_DATE, currentLocalDateTime())
                .set(GAME_SCORE.CREATE_USER_ID, gameScore.getUserId())
                .set(GAME_SCORE.UPDATE_DATE, currentLocalDateTime())
                .set(GAME_SCORE.UPDATE_USER_ID, gameScore.getUserId())
                .execute();
    }

    public void update(GameScore gameScore) {
        dsl.update(GAME_SCORE)
                .set(GAME_SCORE.SCORE, gameScore.getScore().getValue())
                .set(GAME_SCORE.PET_ID, gameScore.getPetId())
                .set(GAME_SCORE.UPDATE_DATE, currentLocalDateTime())
                .set(GAME_SCORE.UPDATE_USER_ID, gameScore.getUserId())
                .where(GAME_SCORE.SCORE_ID.eq(gameScore.getScoreId()))
                .execute();
    }


    /**
     * 주간 게임 Top 3 랭킹 조회 - rank() 사용
     */
    public List<WeeklyRankingDto> findTopRankingsByCurrentWeek(LocalDate currentWeekStartDate) {
        var gs = GAME_SCORE.as("gs");
        var u = USER.as("u");
        var p = PET.as("p");
        var f = FILE_INFO.as("f");

        // rank 필드 정의
        Field<Integer> rankField = DSL.rank()
                .over()
                .orderBy(gs.SCORE.desc())
                .as("ranking");

        // 서브쿼리: 해당 주 데이터에 rank 계산
        Table<?> rankedGs = DSL.select(
                        gs.SCORE_ID,
                        gs.USER_ID,
                        gs.SCORE,
                        gs.PET_ID,
                        rankField
                )
                .from(gs)
                .where(gs.WEEK_START_DATE.eq(currentWeekStartDate))
                .asTable("ranked_gs");

        // rank <= 3 결과만 JOIN
        return dsl.select()
                .from(rankedGs)
                .innerJoin(u).on(rankedGs.field(gs.USER_ID).eq(u.USER_ID).and(u.DELETE_DATE.isNull()))
                .innerJoin(p).on(rankedGs.field(gs.PET_ID).eq(p.PET_ID).and(p.DELETE_DATE.isNull()))
                .innerJoin(f).on(
                        f.REF_TABLE.eq(pet)
                                .and(f.REF_ID.eq(rankedGs.field(gs.PET_ID)))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(rankedGs.field("ranking", Integer.class).le(3))
                .orderBy(
                        rankedGs.field("ranking", Integer.class).asc(),
                        rankedGs.field(gs.SCORE_ID).asc()
                )
                .fetch(record -> mapToWeeklyRankingDto(record, rankedGs, gs, u, p, f));
    }

    /**
     * 주간 게임 내 랭킹 조회 - rank() 사용
     */
    public WeeklyRankingDto findMyRankingsByCurrentWeek(LocalDate currentWeekStartDate, Long userId) {
        var gs = GAME_SCORE.as("gs");
        var u = USER.as("u");
        var p = PET.as("p");
        var f = FILE_INFO.as("f");

        // rank 필드 정의
        Field<Integer> rankField = DSL.rank()
                .over()
                .orderBy(gs.SCORE.desc())
                .as("ranking");

        // 서브쿼리: 해당 주 데이터에 rank 계산
        Table<?> rankedGs = DSL.select(
                        gs.SCORE_ID,
                        gs.USER_ID,
                        gs.SCORE,
                        gs.PET_ID,
                        rankField
                )
                .from(gs)
                .where(gs.WEEK_START_DATE.eq(currentWeekStartDate))
                .asTable("ranked_gs");

        // rank <= 3 결과만 JOIN
        return dsl.select()
                .from(rankedGs)
                .innerJoin(u).on(rankedGs.field(gs.USER_ID).eq(u.USER_ID).and(u.DELETE_DATE.isNull()))
                .innerJoin(p).on(rankedGs.field(gs.PET_ID).eq(p.PET_ID).and(p.DELETE_DATE.isNull()))
                .innerJoin(f).on(
                        f.REF_TABLE.eq(pet)
                                .and(f.REF_ID.eq(rankedGs.field(gs.PET_ID)))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(u.USER_ID.eq(userId))
                .fetchOne(record -> mapToWeeklyRankingDto(record, rankedGs, gs, u, p, f));
    }

    /**
     * Record를 WeeklyRankingDto로 변환
     */
    private WeeklyRankingDto mapToWeeklyRankingDto(
            Record record,
            Table<?> rankedGs,
            kr.co.common.jooq.tables.GameScore gs,
            kr.co.common.jooq.tables.User u,
            kr.co.common.jooq.tables.Pet p,
            kr.co.common.jooq.tables.FileInfo f
    ) {
        return new WeeklyRankingDto(
                record.get(gs.SCORE_ID),
                record.get(rankedGs.field("ranking", Integer.class)),
                record.get(rankedGs.field(gs.USER_ID)),
                record.get(u.NICKNAME),
                record.get(rankedGs.field(gs.SCORE)),
                record.get(rankedGs.field(gs.PET_ID)),
                record.get(p.NAME),
                record.get(f.FILE_URL)
        );
    }

    /**
     * 내 주간 최대 점수 조회
     */
    public Optional<GameScoreDto> findMaxScoreByUserIdAndCurrentWeek(Long userId) {
        var f = FILE_INFO.as("f");
        var p = PET.as("p");
        var u =  USER.as("u");
        return dsl.select(
                        GAME_SCORE.SCORE_ID,
                        GAME_SCORE.USER_ID,
                        GAME_SCORE.WEEK_START_DATE,
                        GAME_SCORE.SCORE,
                        GAME_SCORE.PET_ID,
                        u.NICKNAME,
                        p.NAME,
                        f.FILE_URL
                )
                .from(GAME_SCORE)
                .innerJoin(p).on(GAME_SCORE.PET_ID.eq(p.PET_ID).and(p.DELETE_DATE.isNull()))
                .innerJoin(f).on(
                        f.REF_TABLE.eq(pet)
                                .and(f.REF_ID.eq(GAME_SCORE.PET_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .innerJoin(u).on(u.USER_ID.eq(userId).and(u.DELETE_DATE.isNull()))
                .where(GAME_SCORE.USER_ID.eq(userId)
                        .and(GAME_SCORE.WEEK_START_DATE.eq(field("date_trunc('week', CURRENT_DATE)", LocalDate.class))))
                .fetchOptional(record -> new GameScoreDto(
                        record.get(GAME_SCORE.SCORE_ID),
                        record.get(GAME_SCORE.USER_ID),
                        record.get(GAME_SCORE.WEEK_START_DATE),
                        record.get(GAME_SCORE.SCORE),
                        record.get(GAME_SCORE.PET_ID),
                        record.get(u.NICKNAME),
                        record.get(p.NAME),
                        record.get(f.FILE_URL)
                ));
    }

    /**
     * 게임 점수 삭제
     */

    public void deleteScoreByUserIdAndCurrentWeek(Long userId, Long petId, LocalDate weekStartDate) {
        dsl.delete(GAME_SCORE)
                .where(
                        GAME_SCORE.USER_ID.eq(userId)
                                .and(GAME_SCORE.WEEK_START_DATE.eq(weekStartDate))
                                .and(GAME_SCORE.PET_ID.eq(petId))
                )
                .execute();

    }

}
