package kr.co.api.domain.model.vote;

import kr.co.api.domain.model.pet.Pet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Vote {
    private Long voteId;
    private Pet pet;
    private int dailyVoteCount;
    private int weeklyVoteCount;
    private int monthlyVoteCount;
    private LocalDate voteMonth;
    private String profileImageUrl;

    /**
     * 투표 객체를 생성하는 메서드
     */
    public static Vote createVote(Pet pet, LocalDate voteMonth, String profileImageUrl) {
        return new Vote(null, pet, 0, 0, 0, voteMonth, profileImageUrl);
    }

    /**
     * 투표 객체를 생성하는 메서드
     */
    public static Vote createVoteWithVoteId(Long voteId, Pet pet, int dailyVoteCount,int weeklyVoteCount,int monthlyVoteCount, LocalDate voteMonth, String profileImageUrl) {
        return new Vote(voteId, pet, dailyVoteCount, weeklyVoteCount, monthlyVoteCount, voteMonth, profileImageUrl);
    }
}

