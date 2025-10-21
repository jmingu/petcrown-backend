package kr.co.api.common.service;

import kr.co.api.user.domain.model.UserVoteCount;
import kr.co.api.user.mapper.UserVoteCountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteCountSchedulerService {

    private final UserVoteCountMapper userVoteCountMapper;

    /**
     * 매일 자정에 모든 사용자의 투표 카운트를 1로 초기화
     * 매일 00:00:00에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyVoteCounts() {
        try {
            log.info("Starting daily vote count reset at midnight");

            // 모든 사용자의 투표 카운트를 1로 초기화
            userVoteCountMapper.resetAllVoteCounts();

            log.info("Daily vote count reset completed successfully");
        } catch (Exception e) {
            log.error("Failed to reset daily vote counts", e);
            throw e;
        }
    }
}