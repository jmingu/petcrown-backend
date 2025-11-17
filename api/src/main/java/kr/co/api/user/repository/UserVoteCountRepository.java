package kr.co.api.user.repository;

import kr.co.common.entity.user.UserVoteCountEntity;
import kr.co.common.entity.user.UserVoteCountHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class UserVoteCountRepository {

    private final DSLContext dsl;

    /**
     * 사용자 투표 카운트 저장 (회원가입 시)
     */
    public void insertUserVoteCount(UserVoteCountEntity userVoteCountEntity) {
        dsl.insertInto(USER_VOTE_COUNT)
                .set(USER_VOTE_COUNT.USER_ID, userVoteCountEntity.getUserId())
                .set(USER_VOTE_COUNT.CREATE_DATE, userVoteCountEntity.getCreateDate())
                .set(USER_VOTE_COUNT.CREATE_USER_ID, userVoteCountEntity.getCreateUserId())
                .set(USER_VOTE_COUNT.UPDATE_DATE, userVoteCountEntity.getUpdateDate())
                .set(USER_VOTE_COUNT.UPDATE_USER_ID, userVoteCountEntity.getUpdateUserId())
                .set(USER_VOTE_COUNT.VOTE_COUNT, userVoteCountEntity.getVoteCount())
                .execute();
    }

    /**
     * 사용자 투표 카운트 조회
     */
    public UserVoteCountEntity selectByUserId(Long userId) {
        return dsl.select()
                .from(USER_VOTE_COUNT)
                .where(USER_VOTE_COUNT.USER_ID.eq(userId))
                .fetchOne(this::mapToUserVoteCountEntity);
    }

    /**
     * 투표 카운트 증가 (+1)
     */
    public void incrementVoteCount(Long userId, Long updateUserId) {
        dsl.update(USER_VOTE_COUNT)
                .set(USER_VOTE_COUNT.VOTE_COUNT, USER_VOTE_COUNT.VOTE_COUNT.plus(1))
                .set(USER_VOTE_COUNT.UPDATE_DATE, currentLocalDateTime())
                .set(USER_VOTE_COUNT.UPDATE_USER_ID, updateUserId)
                .where(USER_VOTE_COUNT.USER_ID.eq(userId))
                .execute();
    }

    /**
     * 투표 카운트 감소 (-1)
     */
    public void decrementVoteCount(Long userId, Long updateUserId) {
        dsl.update(USER_VOTE_COUNT)
                .set(USER_VOTE_COUNT.VOTE_COUNT, USER_VOTE_COUNT.VOTE_COUNT.minus(1))
                .set(USER_VOTE_COUNT.UPDATE_DATE, currentLocalDateTime())
                .set(USER_VOTE_COUNT.UPDATE_USER_ID, updateUserId)
                .where(
                        USER_VOTE_COUNT.USER_ID.eq(userId)
                                .and(USER_VOTE_COUNT.VOTE_COUNT.gt(0))
                )
                .execute();
    }

    /**
     * 모든 사용자의 투표 카운트를 1로 초기화 (스케줄러용)
     */
    public void resetAllVoteCounts() {
        dsl.update(USER_VOTE_COUNT)
                .set(USER_VOTE_COUNT.VOTE_COUNT, 1)
                .set(USER_VOTE_COUNT.UPDATE_DATE, currentLocalDateTime())
                .set(USER_VOTE_COUNT.UPDATE_USER_ID, 0L)
                .execute();
    }

    /**
     * 투표 카운트 변경 이력 저장
     */
    public void insertVoteCountHistory(UserVoteCountHistoryEntity historyEntity) {
        dsl.insertInto(USER_VOTE_COUNT_HISTORY)
                .set(USER_VOTE_COUNT_HISTORY.CREATE_DATE, historyEntity.getCreateDate())
                .set(USER_VOTE_COUNT_HISTORY.CREATE_USER_ID, historyEntity.getCreateUserId())
                .set(USER_VOTE_COUNT_HISTORY.USER_ID, historyEntity.getUserId())
                .set(USER_VOTE_COUNT_HISTORY.CHANGE_AMOUNT, historyEntity.getChangeAmount())
                .set(USER_VOTE_COUNT_HISTORY.BEFORE_COUNT, historyEntity.getBeforeCount())
                .set(USER_VOTE_COUNT_HISTORY.AFTER_COUNT, historyEntity.getAfterCount())
                .execute();
    }

    /**
     * Record를 UserVoteCountEntity로 변환
     */
    private UserVoteCountEntity mapToUserVoteCountEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new UserVoteCountEntity(
                record.get(USER_VOTE_COUNT.USER_ID),
                record.get(USER_VOTE_COUNT.CREATE_DATE),
                record.get(USER_VOTE_COUNT.CREATE_USER_ID),
                record.get(USER_VOTE_COUNT.UPDATE_DATE),
                record.get(USER_VOTE_COUNT.UPDATE_USER_ID),
                record.get(USER_VOTE_COUNT.VOTE_COUNT)
        );
    }
}
