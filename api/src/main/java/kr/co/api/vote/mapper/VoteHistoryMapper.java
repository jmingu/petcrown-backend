package kr.co.api.vote.mapper;

import kr.co.common.entity.vote.VoteHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface VoteHistoryMapper {

    /**
     * 투표 기록 저장
     */
    void insertVoteHistory(VoteHistoryEntity voteHistoryEntity);

    /**
     * 오늘 날짜에 특정 사용자가 특정 투표에 참여했는지 확인 (회원)
     */
    int countTodayVoteByUser(@Param("userId") Long userId, @Param("voteId") Long voteId, @Param("historyDate") LocalDate historyDate, @Param("voteCycle") String voteCycle);

    /**
     * 오늘 날짜에 특정 이메일이 특정 투표에 참여했는지 확인 (비회원)
     */
    int countTodayVoteByEmail(@Param("email") String email, @Param("voteId") Long voteId, @Param("historyDate") LocalDate historyDatem, @Param("voteCycle") String voteCycle);
}