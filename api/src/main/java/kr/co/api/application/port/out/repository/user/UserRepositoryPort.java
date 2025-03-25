package kr.co.api.application.port.out.repository.user;

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
    void save(User user);
}

