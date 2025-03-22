package kr.co.api.application.port.out.repository.user;

import kr.co.api.adapter.out.persistence.repository.user.JpaUserRepository;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;

public interface UserRepository {
    void save(User user);
}

