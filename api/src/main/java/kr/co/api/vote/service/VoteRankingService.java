package kr.co.api.vote.service;

import kr.co.api.common.repository.CommonDateRepository;
import kr.co.api.vote.dto.command.VoteInfoDto;
import kr.co.api.vote.dto.command.VoteRankInfoDto;
import kr.co.api.vote.repository.VoteRankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VoteRankingService {

    private final VoteRankingRepository voteRankingRepository;
    private final CommonDateRepository commonDateRepository;

    /**
     * 이번 주 Top 3 랭킹 조회 (실시간) - date_trunc 사용
     */
    public List<VoteRankInfoDto> getCurrentWeekTopRanking() {

        LocalDate weekStartDate = commonDateRepository.selectCurrentWeekStartDate();

        List<VoteRankInfoDto> ranking = voteRankingRepository.selectCurrentWeekTopRanking(weekStartDate);
        log.info("Current week top 3 ranking retrieved: count={}", ranking.size());

        return ranking;
    }

    /**
     * 지난 주 Top 3 랭킹 조회 - date_trunc 사용
     */
    public List<VoteRankInfoDto> getLastWeekTopRanking() {
        LocalDate weekStartDate = commonDateRepository.selectLastWeekStartDate();

        List<VoteRankInfoDto> ranking = voteRankingRepository.selectCurrentWeekTopRanking(weekStartDate);
        log.info("Last week top 3 ranking retrieved: count={}", ranking.size());

        return ranking;
    }

    /**
     * 내 주간 랭킹
     */
    public VoteRankInfoDto getCurrentWeekMyRanking(Long userId) {
        LocalDate weekStartDate = commonDateRepository.selectCurrentWeekStartDate();

        VoteRankInfoDto ranking = voteRankingRepository.selectCurrentWeekMyRanking(weekStartDate, userId);

        return ranking;
    }
}
