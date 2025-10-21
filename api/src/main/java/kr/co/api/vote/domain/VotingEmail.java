package kr.co.api.vote.domain;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VotingEmail {

    private final String emailAddress;
    private final boolean isMember;
    private final Long userId;

    /**
     * 회원 이메일로 생성
     */
    public static VotingEmail createForMember(String emailAddress, Long userId) {
        validateEmailAddress(emailAddress);
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        return new VotingEmail(emailAddress, true, userId);
    }

    /**
     * 비회원 이메일로 생성
     */
    public static VotingEmail createForGuest(String emailAddress) {
        validateEmailAddress(emailAddress);

        return new VotingEmail(emailAddress, false, null);
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

        if (userId == null) {
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

    /**
     * 이메일 주소 유효성 검증
     */
    private static void validateEmailAddress(String emailAddress) {
        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 간단한 이메일 형식 검증
        if (!emailAddress.contains("@")) {
            throw new PetCrownException(BusinessCode.INVALID_FORMAT);
        }
    }
}