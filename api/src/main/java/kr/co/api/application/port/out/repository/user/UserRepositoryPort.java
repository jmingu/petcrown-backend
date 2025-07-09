package kr.co.api.application.port.out.repository.user;

import kr.co.api.domain.model.user.Email;
import kr.co.api.domain.model.user.User;

public interface UserRepositoryPort {

    /**
     * 이메일 중복 검사
     */
    User findByEmail(String email);

    /**
     * 닉네임 중복 검사
     */
    User findByNickname(String nickname);

    /**
     * 회원가입
     */
    User saveUser(User user, Email email);

    /**
     * user id로 Email 조회
     */
    Email findEmailByUserId(Long userId);

    /**
     * user id로 User 조회
     */
    User findUserByUserId(Long userId);

    /**
     * 사용자 인증정보 업데이트
     */
    void updateEmailVerificationStatus(Long userId, String isEmailVerified);

    /**
     * 이메일 인증정보 저장
     */
    void saveEmailVerification(Email emailObject);

    /**
     * 사용자 정보 변경
     */
    void changeUserInfo(User user);



}

