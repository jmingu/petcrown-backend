package kr.co.api.adapter.out.persistence.repository.user;

import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserRepository;
import kr.co.api.application.port.out.repository.user.UserRepositoryPort;
import kr.co.api.converter.user.UserEntityConverter;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final UserEntityConverter userEntityConverter;

    /**
     * 이메일 중복 검증
     */
    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> userEntity = jpaUserRepository.findByEmail(email);

        // userEntity가 존재하면 User로 변환하여 반환
        if (userEntity.isPresent()) {
            User user = userEntityConverter.toDomain(userEntity.get());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * 회원가입
     */
    @Override
    public void save(User user) {
        UserEntity entity = userEntityConverter.emailResistrsationToEntity(user);
        jpaUserRepository.save(entity);
    }

}
