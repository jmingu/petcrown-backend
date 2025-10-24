package kr.co.api.vote.mapper;

import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.common.entity.vote.VoteFileInfoEntity;
import kr.co.common.entity.vote.VoteMonthlyEntity;
import kr.co.common.entity.vote.VoteWeeklyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Vote Mapper Interface - Weekly/Monthly 구조
 */
@Mapper
public interface VoteMapper {

    /**
     * Weekly 투표 등록
     */
    int insertVoteWeekly(VoteWeeklyEntity voteWeeklyEntity);

    /**
     * Monthly 투표 등록
     */
    int insertVoteMonthly(VoteMonthlyEntity voteMonthlyEntity);

    /**
     * 투표 파일 정보 등록
     */
    int insertVoteFileInfo(VoteFileInfoEntity voteFileInfoEntity);

    /**
     * Weekly 투표 조회 (petId와 weekStartDate로)
     */
    VoteWeeklyEntity selectVoteWeeklyByPetIdAndWeek(@Param("petId") Long petId,
                                                      @Param("weekStartDate") LocalDate weekStartDate);

    /**
     * Monthly 투표 조회 (petId와 monthStartDate로)
     */
    VoteMonthlyEntity selectVoteMonthlyByPetIdAndMonth(@Param("petId") Long petId,
                                                         @Param("monthStartDate") LocalDate monthStartDate);

    /**
     * Weekly 투표 목록 조회 (현재 주)
     */
    List<VoteInfoDto> selectVoteWeeklyList(@Param("weekStartDate") LocalDate weekStartDate,
                                            @Param("offset") long offset,
                                            @Param("limit") int limit);

    /**
     * Weekly 투표 목록 개수
     */
    int selectVoteWeeklyListCount(@Param("weekStartDate") LocalDate weekStartDate);

    /**
     * Weekly 투표 상세 조회
     */
    VoteInfoDto selectVoteWeeklyDetail(@Param("voteId") Long voteId);

    /**
     * Monthly 투표 목록 조회 (현재 월)
     */
    List<VoteInfoDto> selectVoteMonthlyList(@Param("monthStartDate") LocalDate monthStartDate,
                                             @Param("offset") long offset,
                                             @Param("limit") int limit);

    /**
     * Monthly 투표 목록 개수
     */
    int selectVoteMonthlyListCount(@Param("monthStartDate") LocalDate monthStartDate);

    /**
     * Monthly 투표 상세 조회
     */
    VoteInfoDto selectVoteMonthlyDetail(@Param("voteMonthlyId") Long voteMonthlyId);

    /**
     * Weekly 투표 카운트 증가
     */
    void updateVoteWeeklyCount(@Param("voteWeeklyId") Long voteWeeklyId,
                                @Param("updateUserId") Long updateUserId);

    /**
     * Monthly 투표 카운트 증가
     */
    void updateVoteMonthlyCount(@Param("voteMonthlyId") Long voteMonthlyId,
                                 @Param("updateUserId") Long updateUserId);

    /**
     * Weekly 투표 삭제 (논리 삭제)
     */
    void deleteVoteWeekly(@Param("voteWeeklyId") Long voteWeeklyId,
                           @Param("deleteUserId") Long deleteUserId);

    /**
     * Monthly 투표 삭제 (논리 삭제)
     */
    void deleteVoteMonthly(@Param("voteMonthlyId") Long voteMonthlyId,
                            @Param("deleteUserId") Long deleteUserId);

    /**
     * 투표 파일 정보 수정
     */
    void updateVoteFileInfo(VoteFileInfoEntity voteFileInfoEntity);

    /**
     * 투표 파일 정보 삭제 (논리 삭제)
     */
    void deleteVoteFileInfo(@Param("refTable") String refTable,
                             @Param("refId") Long refId,
                             @Param("deleteUserId") Long deleteUserId);

    /**
     * 사용자의 현재 주 투표 등록 수 조회 (date_trunc 사용)
     */
    int countWeeklyVoteRegistrationByUser(@Param("userId") Long userId);

    /**
     * Weekly 투표 수정
     */
    void updateVoteWeekly(VoteWeeklyEntity voteWeeklyEntity);

    /**
     * 현재 주의 weekStartDate 조회 (date_trunc 사용)
     */
    LocalDate selectCurrentWeekStartDate();
}
