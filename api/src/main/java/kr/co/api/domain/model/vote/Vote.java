package kr.co.api.domain.model.vote;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Vote {
    private Long voteId;
    private Long petId;
    private int dailyVoteCount;
    private int weeklyVoteCount;
    private int monthlyVoteCount;
    private LocalDate voteMonth;
    private String profileImageUrl;

    /**
     * 투표 객체를 생성하는 메서드
     */
    public static Vote createVote(Long petId, LocalDate voteMonth, String profileImageUrl) {
        if (petId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        if (voteMonth == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        return new Vote(null, petId, 0, 0, 0, voteMonth, profileImageUrl);
    }

    /**
     * 모든 필드로 투표 객체를 생성하는 메서드
     */
    public static Vote createVoteWithVoteId(Long voteId, Long petId, int dailyVoteCount, int weeklyVoteCount, int monthlyVoteCount, LocalDate voteMonth, String profileImageUrl) {
        return new Vote(voteId, petId, dailyVoteCount, weeklyVoteCount, monthlyVoteCount, voteMonth, profileImageUrl);
    }
    
    /**
     * 일일 투표 수 증가
     */
    public void increaseDailyVoteCount() {
        this.dailyVoteCount++;
    }
    
    /**
     * 주간 투표 수 증가
     */
    public void increaseWeeklyVoteCount() {
        this.weeklyVoteCount++;
    }
    
    /**
     * 월간 투표 수 증가
     */
    public void increaseMonthlyVoteCount() {
        this.monthlyVoteCount++;
    }
    
    /**
     * 현재 월 투표인지 확인
     */
    public boolean isCurrentMonthVote() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return voteMonth.equals(currentMonth);
    }
    
    /**
     * 과거 월 투표인지 확인
     */
    public boolean isPastMonthVote() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return voteMonth.isBefore(currentMonth);
    }
    
    /**
     * 총 투표 수 계산
     */
    public int getTotalVoteCount() {
        return monthlyVoteCount;
    }
}

