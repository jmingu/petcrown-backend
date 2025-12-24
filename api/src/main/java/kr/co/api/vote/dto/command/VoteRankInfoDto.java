package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class VoteRankInfoDto {

    private final Long voteId;
    private final Long petId;
    private final Long userId;
    private final int rank;
    private final String nickname;
    private final String name;
    private final String gender;
    private final LocalDate birthDate;
    private final Integer breedId;
    private final String breedName;
    private final String customBreed;
    private final Integer speciesId;
    private final String speciesName;
    private final Integer petModeId;
    private final String petModeName;
    private final int dailyVoteCount;
    private final int weeklyVoteCount;
    private final int monthlyVoteCount;
    private final LocalDate voteMonth;
    private final String profileImageUrl;
    private final String ownerEmail;
}