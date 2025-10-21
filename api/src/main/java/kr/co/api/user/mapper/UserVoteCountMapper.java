package kr.co.api.user.mapper;

import kr.co.common.entity.user.UserVoteCountEntity;
import kr.co.common.entity.user.UserVoteCountHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserVoteCountMapper {

    /**
     * 사용자 투표 카운트 저장 (회원가입 시)
     */
    void insertUserVoteCount(UserVoteCountEntity userVoteCountEntity);

    /**
     * 사용자 투표 카운트 조회
     */
    UserVoteCountEntity selectByUserId(@Param("userId") Long userId);

    /**
     * 투표 카운트 증가 (+1)
     */
    void incrementVoteCount(@Param("userId") Long userId, @Param("updateUserId") Long updateUserId);

    /**
     * 투표 카운트 감소 (-1)
     */
    void decrementVoteCount(@Param("userId") Long userId, @Param("updateUserId") Long updateUserId);

    /**
     * 모든 사용자의 투표 카운트를 1로 초기화 (스케줄러용)
     */
    void resetAllVoteCounts();

    /**
     * 투표 카운트 변경 이력 저장
     */
    void insertVoteCountHistory(UserVoteCountHistoryEntity historyEntity);
}