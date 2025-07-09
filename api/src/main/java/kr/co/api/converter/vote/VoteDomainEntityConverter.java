package kr.co.api.converter.vote;

import kr.co.api.converter.pet.PetDomainEntityConverter;
import kr.co.api.domain.model.vote.Vote;
import kr.co.common.entity.vote.VoteEntity;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static kr.co.common.enums.BusinessCode.MISSING_REQUIRED_VALUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteDomainEntityConverter {
    private final PetDomainEntityConverter petDomainEntityConverter;

    /**
     * Vote 도메인 객체를 VoteEntity로 변환
     */
    public VoteEntity toEntity(Vote vote) {

        if (vote == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);

        }

        return VoteEntity.createVote(petDomainEntityConverter.toEntityWithUser(vote.getPet()), vote.getDailyVoteCount(), vote.getWeeklyVoteCount(), vote.getMonthlyVoteCount(), vote.getVoteMonth(), vote.getProfileImageUrl());
    }

    /**
     * VoteEntity를 Vote 도메인 객체로 변환
     * pet 필드는 null로 설정
     */
    public Vote toDomainWithNullPet(VoteEntity voteEntity) {

        if (voteEntity == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }

        return Vote.createVoteWithVoteId(voteEntity.getVoteId(), null, voteEntity.getDailyVoteCount(), voteEntity.getWeeklyVoteCount(), voteEntity.getMonthlyVoteCount(), voteEntity.getVoteMonth(), voteEntity.getProfileImageUrl());
    }
}
