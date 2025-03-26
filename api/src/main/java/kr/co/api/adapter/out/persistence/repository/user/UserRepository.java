package kr.co.api.adapter.out.persistence.repository.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserEmailVerificationRepository;
import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserRepository;
import kr.co.api.application.port.out.repository.user.UserRepositoryPort;
import kr.co.api.converter.email.EmailEntityConverter;
import kr.co.api.converter.user.UserEntityConverter;
import kr.co.api.domain.model.Email;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEmailVerificationEntity;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepository implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final UserEntityConverter userEntityConverter;
    private final JpaUserEmailVerificationRepository jpaUserEmailVerificationRepository;
    private final EmailEntityConverter emailEntityConverter;

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
    @Transactional
    public User register(User user, Email email) {
        // 회원가입
        UserEntity entity = userEntityConverter.userToEntity(user);
        UserEntity saveUser = jpaUserRepository.save(entity);

        // 인증코드 저장
        UserEmailVerificationEntity userEmailVerificationEntity = emailEntityConverter.EmailToUserEmailVerificationEntity(email, saveUser);
        jpaUserEmailVerificationRepository.save(userEmailVerificationEntity);

        User newUser = userEntityConverter.toDomain(saveUser);

        return newUser;
    }

    /**
     * 이메일 인증코드 조회
     */
    @Override
    public Optional<Email> findEmailByUserId(Long userId) {
        Optional<UserEmailVerificationEntity> entity = jpaUserEmailVerificationRepository.findByUserId(userId);

        // userEntity가 존재하면 User로 변환하여 반환
        if (entity.isPresent()) {
            Email email = emailEntityConverter.userEmailVerificationEntityToEmail(entity.get());
            return Optional.of(email);
        }
        return Optional.empty();

    }

    /**
     * 사용자 인증정보 업데이트
     */
    @Transactional
    @Override
    public void updateEmailVerificationStatus(Long userId, String isEmailVerified) {
        jpaUserRepository.updateEmailVerificationStatus(userId, isEmailVerified);
    }

    /**
     * 이메일 인증 저장
     */
    @Transactional
    @Override
    public void saveEmailVerification(Email emailObject) {
        Optional<UserEntity> userEntity = jpaUserRepository.findByEmail(emailObject.getUser().getEmail());

        // 사용자 없으면 예외 발생
        userEntity.orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));

        Optional<UserEmailVerificationEntity> emailEntity = jpaUserEmailVerificationRepository.findByUserId(userEntity.get().getUserId());

        // 있으면 업데이트
        emailEntity.ifPresentOrElse(
                entity -> jpaUserEmailVerificationRepository.updateVerificationInfo(
                        entity.getEmailVerificationId(),
                        emailObject.getVerificationCode(),
                        emailObject.getExpiresDate(),
                        userEntity.get().getUserId()
                ),
                () -> {
                    // 없으면 저장
                    UserEmailVerificationEntity userEmailVerificationEntity = emailEntityConverter.EmailToUserEmailVerificationEntity(emailObject, userEntity.get());
                    jpaUserEmailVerificationRepository.save(userEmailVerificationEntity);
                }
        );

    }


}
