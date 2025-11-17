package kr.co.api.vote.repository;

import kr.co.common.entity.vote.VoteHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class VoteHistoryRepository {

    private final DSLContext dsl;

    /**
     * 투표 기록 저장 (DB의 current_date 사용)
     */
    public void insertVoteHistory(VoteHistoryEntity voteHistoryEntity) {
        dsl.insertInto(VOTE_HISTORY)
                .set(VOTE_HISTORY.USER_ID, voteHistoryEntity.getUserId())
                .set(VOTE_HISTORY.VOTE_ID, voteHistoryEntity.getVoteId())
                .set(VOTE_HISTORY.HISTORY_DATE, currentDate().cast(LocalDate.class))  // DB 기준 날짜 사용
                .set(VOTE_HISTORY.EMAIL, voteHistoryEntity.getEmail())
                .set(VOTE_HISTORY.CREATE_DATE, currentLocalDateTime())  // DB 기준 시간 사용
                .set(VOTE_HISTORY.VOTE_CYCLE, kr.co.common.jooq.enums.VoteCycleType.valueOf(voteHistoryEntity.getVoteCycle()))
                .execute();
    }

    /**
     * 오늘 날짜에 특정 사용자가 특정 투표에 참여했는지 확인 (회원) - DB의 current_date 사용
     */
    public int countTodayVoteByUser(Long userId, Long voteId, String voteCycle) {
        Integer count = dsl.selectCount()
                .from(VOTE_HISTORY)
                .where(
                        VOTE_HISTORY.USER_ID.eq(userId)
                                .and(VOTE_HISTORY.VOTE_ID.eq(voteId))
                                .and(VOTE_HISTORY.HISTORY_DATE.eq(currentDate().cast(LocalDate.class)))  // DB 기준 날짜 비교
                                .and(VOTE_HISTORY.VOTE_CYCLE.eq(kr.co.common.jooq.enums.VoteCycleType.valueOf(voteCycle)))
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 오늘 날짜에 특정 이메일이 특정 투표에 참여했는지 확인 (비회원) - DB의 current_date 사용
     */
    public int countTodayVoteByEmail(String email, Long voteId, String voteCycle) {
        Integer count = dsl.selectCount()
                .from(VOTE_HISTORY)
                .where(
                        VOTE_HISTORY.EMAIL.eq(email)
                                .and(VOTE_HISTORY.VOTE_ID.eq(voteId))
                                .and(VOTE_HISTORY.HISTORY_DATE.eq(currentDate().cast(LocalDate.class)))  // DB 기준 날짜 비교
                                .and(VOTE_HISTORY.VOTE_CYCLE.eq(kr.co.common.jooq.enums.VoteCycleType.valueOf(voteCycle)))
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }
}
