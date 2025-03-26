package kr.co.api.application.port.out.repository.user;

import kr.co.api.domain.model.Email;
import kr.co.api.domain.model.user.User;

import java.util.Optional;

public interface UserRepositoryPort {

    /**
     * 이메일 중복 검사
     */
    Optional<User> findByEmail(String email);

    /**
     * 회원가입
     */
    User register(User user, Email email);

    /**
     * 이메일 인증 조회
     */
    Optional<Email> findEmailByUserId(Long userId);

    /**
     * 사용자 인증정보 업데이트
     */
    void updateEmailVerificationStatus(Long userId, String isEmailVerified);

    /**
     * 이메일 인증정보 저장
     */
    void saveEmailVerification(Email emailObject);




}

