package kr.co.api.adapter.out.persistence.repository.user;

import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.user.vo.Email;
import kr.co.api.domain.model.user.vo.Nickname;
import kr.co.api.domain.repository.UserRepository;
import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserRepository;
import kr.co.api.converter.user.UserDomainEntityConverter;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    
    private final JpaUserRepository jpaUserRepository;
    private final UserDomainEntityConverter userConverter;
    
    @Override
    public User save(User user) {
        UserEntity userEntity = userConverter.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        return userConverter.toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(Long userId) {
        return jpaUserRepository.findById(userId)
                .map(userConverter::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaUserRepository.findByEmail(email.getValue())
                .map(userConverter::toDomain);
    }
    
    @Override
    public Optional<User> findByNickname(Nickname nickname) {
        return jpaUserRepository.findByNickname(nickname.getValue())
                .map(userConverter::toDomain);
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return jpaUserRepository.existsByEmail(email.getValue());
    }
    
    @Override
    public boolean existsByNickname(Nickname nickname) {
        return jpaUserRepository.existsByNickname(nickname.getValue());
    }
    
    @Override
    public void delete(User user) {
        UserEntity userEntity = userConverter.toEntity(user);
        jpaUserRepository.delete(userEntity);
    }
    
    @Override
    public void deleteById(Long userId) {
        jpaUserRepository.deleteById(userId);
    }
}