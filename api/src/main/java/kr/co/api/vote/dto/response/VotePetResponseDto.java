package kr.co.api.vote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
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
    private Integer petModeId;
    private String petModeName;
    private int dailyVoteCount;
    private int weeklyVoteCount;
    private int monthlyVoteCount;
    private LocalDate voteMonth;
    private String profileImageUrl;
    private String ownerEmail; // 투표를 등록한 사용자의 이메일
}