package kr.co.api.application.dto.vote.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VotePetResponseDto {
    private Long voteId;
    private Long petId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private Integer breedId;
    private String breedName;
    private Integer speciesId;
    private String speciesName;
    private int dailyVoteCount;
    private int weeklyVoteCount;
    private int monthlyVoteCount;
    private LocalDate voteMonth;
    private String profileImageUrl;

}
