package kr.co.api.vote.service;

import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.repository.VoteRankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VoteRankingService {

    private final VoteRankingRepository voteRankingRepository;

    /**
     * 이번 주 Top 3 랭킹 조회 (실시간) - date_trunc 사용
     */
    public List<VoteInfoDto> getCurrentWeekTopRanking() {
        List<VoteInfoDto> ranking = voteRankingRepository.selectCurrentWeekTopRanking();
        log.info("Current week top 3 ranking retrieved: count={}", ranking.size());

        return ranking;
    }

    /**
     * 지난 주 Top 3 랭킹 조회 - date_trunc 사용
     */
    public List<VoteInfoDto> getLastWeekTopRanking() {
        List<VoteInfoDto> ranking = voteRankingRepository.selectLastWeekTopRanking();
        log.info("Last week top 3 ranking retrieved: count={}", ranking.size());

        return ranking;
    }
}
