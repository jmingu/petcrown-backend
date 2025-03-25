package kr.co.api.application.port.in.user;

import kr.co.api.domain.model.user.User;

public interface UserUseCase {

    /**
     * 이메일 중복검사
     */
    void checkEmailDuplication(String email);

    /**
     * 회원가입
     */
    void save(User user);
}
