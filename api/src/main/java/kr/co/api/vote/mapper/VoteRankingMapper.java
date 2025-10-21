package kr.co.api.vote.mapper;

import kr.co.api.vote.dto.command.VoteInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Vote Ranking Mapper Interface
 */
@Mapper
public interface VoteRankingMapper {

    /**
     * 이번 주 Top 3 랭킹 조회 (실시간) - date_trunc 사용
     */
    List<VoteInfoDto> selectCurrentWeekTopRanking();

    /**
     * 지난 주 Top 3 랭킹 조회 - date_trunc 사용
     */
    List<VoteInfoDto> selectLastWeekTopRanking();
}
