package kr.co.api.application.port.in.user;

import kr.co.api.domain.model.user.User;

public interface UserUseCase {

    void save(User user);
}
