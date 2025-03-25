package kr.co.api.application.port.out.repository.email;

import kr.co.api.domain.model.Email;
import kr.co.api.domain.model.user.User;

import java.util.Optional;

public interface EmailRepositoryPort {

    /**
     * 이메일 인증번호 저장
     */
    void save(Email email);

}

