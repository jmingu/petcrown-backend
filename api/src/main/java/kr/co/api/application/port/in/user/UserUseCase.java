package kr.co.api.application.port.in.user;

import kr.co.api.domain.model.user.User;

public interface UserUseCase {

    /**
     * 이메일 인증
     */
    void sendVerificationCode(String email);

    /**
     * 회원가입
     */
    void save(User user);
}
