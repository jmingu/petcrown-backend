package kr.co.api.domain.service;

import kr.co.api.domain.model.pet.Pet;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.vote.Vote;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VoteDomainService {
    
    public void validateVoteRegistration(Vote vote, Pet pet, User user) {
        if (vote == null || pet == null || user == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        validateVoteBusinessRules(vote, pet, user);
    }
    
    public boolean canRegisterVoteForPet(Pet pet, LocalDate voteMonth) {
        if (pet == null || voteMonth == null) {
            return false;
        }
        
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return !voteMonth.isBefore(currentMonth);
    }
    
    public boolean canDeletePetWithVote(Pet pet, LocalDate voteMonth) {
        if (pet == null || voteMonth == null) {
            return true;
        }
        
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return voteMonth.isBefore(currentMonth);
    }
    
    public LocalDate getCurrentVoteMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }
    
    public boolean isVoteActive(LocalDate voteMonth) {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return voteMonth.equals(currentMonth) || voteMonth.isAfter(currentMonth);
    }
    
    private void validateVoteBusinessRules(Vote vote, Pet pet, User user) {
        if (!pet.getUserId().equals(user.getUserId())) {
            throw new PetCrownException(BusinessCode.PET_NOT_OWNED);
        }
        
        if (vote.getVoteMonth() == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        if (vote.getVoteMonth().isBefore(currentMonth)) {
            throw new PetCrownException(BusinessCode.INVALID_VOTE_MONTH);
        }
    }
}