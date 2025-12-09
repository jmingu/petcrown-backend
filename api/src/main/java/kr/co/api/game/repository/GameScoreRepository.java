package kr.co.api.game.repository;

import kr.co.api.game.domain.model.GameScore;
import kr.co.api.game.dto.command.GameScoreDto;
import kr.co.api.game.dto.command.WeeklyRankingDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
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
     * 주간 게임 랭킹
     */
    public List<WeeklyRankingDto> findTopRankingsByCurrentWeek(int limit) {
        var gs = GAME_SCORE.as("gs");
        var u = USER.as("u");
        var p = PET.as("p");
        var f = FILE_INFO.as("f");

        return dsl.select(
                        rowNumber().over(orderBy(gs.SCORE.desc())).as("ranking"),
                        gs.USER_ID,
                        u.NICKNAME,
                        gs.SCORE,
                        gs.PET_ID,
                        p.NAME,
                        f.FILE_URL
                )
                .from(gs)
                .innerJoin(u).on(gs.USER_ID.eq(u.USER_ID).and(u.DELETE_DATE.isNull()))
                .innerJoin(p).on(gs.PET_ID.eq(p.PET_ID).and(p.DELETE_DATE.isNull()))
                .innerJoin(f).on(
                        f.REF_TABLE.eq(pet)
                                .and(f.REF_ID.eq(gs.PET_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(gs.WEEK_START_DATE.eq(field("date_trunc('week', CURRENT_DATE)", LocalDate.class)))
                .orderBy(gs.SCORE.desc())
                .limit(limit)
                .fetch(record -> new WeeklyRankingDto(
                        record.get("ranking", Integer.class),
                        record.get(gs.USER_ID),
                        record.get(u.NICKNAME),
                        record.get(gs.SCORE),
                        record.get(gs.PET_ID),
                        record.get(p.NAME),
                        record.get(f.FILE_URL)
                ));
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
     * 지난주 랭킹 조회
     */
    public List<WeeklyRankingDto> findLastWeekRankings(int limit) {


        var gs = GAME_SCORE.as("gs");
        var u = USER.as("u");
        var p = PET.as("p");
        var f = FILE_INFO.as("f");

        return dsl.select(
                        rowNumber().over(orderBy(gs.SCORE.desc())).as("ranking"),
                        gs.USER_ID,
                        u.NICKNAME,
                        gs.SCORE,
                        gs.PET_ID,
                        f.FILE_URL
                )
                .from(gs)
                .innerJoin(u).on(gs.USER_ID.eq(u.USER_ID).and(u.DELETE_DATE.isNull()))
                .innerJoin(p).on(gs.PET_ID.eq(p.PET_ID).and(p.DELETE_DATE.isNull()))
                .innerJoin(f).on(
                        f.REF_TABLE.eq(pet)
                                .and(f.REF_ID.eq(gs.PET_ID))
                                .and(f.FILE_TYPE.eq(IMAGE))
                                .and(f.DELETE_DATE.isNull())
                )
                .where(gs.WEEK_START_DATE.eq(
                        field("date_trunc('week', CURRENT_DATE) - interval '1 week'", LocalDate.class)
                ))
                .orderBy(gs.SCORE.desc())
                .limit(limit)
                .fetch(record -> new WeeklyRankingDto(
                        record.get("ranking", Integer.class),
                        record.get(gs.USER_ID),
                        record.get(u.NICKNAME),
                        record.get(gs.SCORE),
                        record.get(gs.PET_ID),
                        record.get(p.NAME),
                        record.get(f.FILE_URL)
                ));

    }
}
