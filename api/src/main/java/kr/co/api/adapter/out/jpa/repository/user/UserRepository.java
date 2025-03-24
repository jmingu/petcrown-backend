package kr.co.api.adapter.out.jpa.repository.user;

import kr.co.api.converter.user.UserEntityConverter;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository implements kr.co.api.application.port.out.repository.user.UserRepository {

    private final JpaUserRepository userRepository;
    private final UserEntityConverter userEntityConverter;

    @Override
    public void save(User user) {
        UserEntity entity = userEntityConverter.toEntity(user);
        userRepository.save(entity);
    }

}
