package kr.co.api.vote.domain.model;

import kr.co.api.user.domain.model.User;
import kr.co.api.user.domain.vo.Email;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VotingEmail {

    private final Email email;
    private final boolean isMember;
    private final User user;

    /**
     * 회원 이메일로 생성
     */
    public static VotingEmail createForMember(String emailAddress, Long userId) {
        Email email = Email.of(emailAddress);
        User user = User.ofId(userId);

        return new VotingEmail(email, true, user);
    }

    /**
     * 비회원 이메일로 생성
     */
    public static VotingEmail createForGuest(String emailAddress) {
        Email email = Email.of(emailAddress);

        return new VotingEmail(email, false, null);
    }

    /**
     * 회원인지 확인
     */
    public boolean isMember() {
        return isMember;
    }

    /**
     * 비회원인지 확인
     */
    public boolean isGuest() {
        return !isMember;
    }

    /**
     * 회원 투표 권한 검증
     */
    public void validateMemberVotingRight() {
        if (!isMember()) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        if (user == null || user.getUserId() == null) {
            throw new PetCrownException(BusinessCode.INVALID_USER_UPDATE);
        }
    }

    /**
     * 비회원 투표 권한 검증 (오늘 인증된 이메일인지)
     */
    public void validateGuestVotingRight(boolean isTodayVerified) {
        if (isMember()) {
            throw new PetCrownException(BusinessCode.EMAIL_ALREADY_REGISTERED);
        }

        if (!isTodayVerified) {
            throw new PetCrownException(BusinessCode.EMAIL_NOT_VERIFIED_TODAY);
        }
    }
}