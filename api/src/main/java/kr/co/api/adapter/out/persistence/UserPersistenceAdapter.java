package kr.co.api.adapter.out.persistence;

import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserRepository;
import kr.co.api.application.dto.user.response.UserInfoResponseDto;
import kr.co.api.converter.user.UserDomainEntityConverter;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.user.vo.Email;
import kr.co.api.domain.model.user.vo.Nickname;
import kr.co.api.domain.port.out.LoadUserPort;
import kr.co.api.domain.port.out.SaveUserPort;
import kr.co.common.entity.user.UserEntity;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 사용자 영속성 어댑터 (Output Adapter)
 * LoadUserPort, SaveUserPort 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {
    
    private final JpaUserRepository jpaUserRepository;
    private final UserDomainEntityConverter userConverter;
    
    // LoadUserPort 구현
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
    public UserInfoResponseDto getUserInfo(Long userId) {
        UserEntity userEntity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.MEMBER_NOT_FOUND));
        
        return new UserInfoResponseDto(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getName(),
                userEntity.getNickname(),
                userEntity.getPhoneNumber(),
                userEntity.getBirthDate(),
                userEntity.getGender(),
                userEntity.getProfileImageUrl()
        );
    }
    
    // SaveUserPort 구현
    @Override
    public User save(User user) {
        UserEntity userEntity = userConverter.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        return userConverter.toDomain(savedEntity);
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
    
    @Override
    public void updateUserInfo(User user, Long userId) {
        UserEntity existingEntity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.MEMBER_NOT_FOUND));
        
        // 업데이트할 필드들 설정
        existingEntity.updateUserInfo(
                user.getNameValue(),
                user.getNicknameValue(),
                user.getPhoneNumberValue(),
                user.getBirthDate(),
                user.getGenderValue()
        );
        
        jpaUserRepository.save(existingEntity);
    }
}