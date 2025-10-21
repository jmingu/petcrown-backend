package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteRegistrationDto {

    private final Long userId;
    private final Long petId;
    private final String profileImageUrl;
    private final Integer petModeId;
    private final Long voteId; // 수정 시에만 사용

    // 등록용 생성자 (voteId 없이)
    public VoteRegistrationDto(Long userId, Long petId, String profileImageUrl, Integer petModeId) {
        this.userId = userId;
        this.petId = petId;
        this.profileImageUrl = profileImageUrl;
        this.petModeId = petModeId;
        this.voteId = null;
    }
}