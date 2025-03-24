package kr.co.api.application.port.out.repository.user;

import kr.co.api.domain.model.user.User;

public interface UserRepository {
    void save(User user);
}

