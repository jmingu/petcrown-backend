package kr.co.api.adapter.out.persistence.repository.user;

import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserEmailVerificationRepository;
import kr.co.api.adapter.out.persistence.repository.user.jpa.JpaUserRepository;
import kr.co.api.application.port.out.repository.user.UserRepositoryPort;
import kr.co.api.converter.user.EmailDomainEntityConverter;
import kr.co.api.converter.user.UserDomainEntityConverter;
import kr.co.api.domain.model.user.Email;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.EmailVerificationEntity;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j

public class UserRepository implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final UserDomainEntityConverter userConverter;
    private final JpaUserEmailVerificationRepository jpaUserEmailVerificationRepository;
    private final EmailDomainEntityConverter emailConverter;

    /**
     * 이메일 중복 검증
     */
    @Override
    public User findByEmail(String email) {
        Optional<UserEntity> userEntity = jpaUserRepository.findByEmail(email);

        // userEntity가 존재하면 User로 변환하여 반환
        if (userEntity.isPresent()) {
            User user = userConverter.toDomainBasic(userEntity.get());
            return user;
        }
        return null;
    }

    @Override
    public User findByNickname(String nickname) {
        Optional<UserEntity> userEntity = jpaUserRepository.findByNickname(nickname);

        // userEntity가 존재하면 User로 변환하여 반환
        if (userEntity.isPresent()) {
            User user = userConverter.toDomainBasic(userEntity.get());
            return user;
        }
        return null;
    }

    /**
     * 회원가입
     */
    @Override
    public User saveUser(User user, Email email) {
        // 회원가입
        UserEntity saveUser = jpaUserRepository.save(userConverter.registerUserToEntity(user));

        // 인증코드 저장
        jpaUserEmailVerificationRepository.save(emailConverter.EmailToUserEmailVerificationEntity(email, saveUser));

        User newUser = userConverter.toDomainBasic(saveUser);

        return newUser;
    }

    /**
     * user id로 Email 조회
     */
    @Override
    public Email findEmailByUserId(Long userId) {
        Optional<EmailVerificationEntity> entity = jpaUserEmailVerificationRepository.findEmailByUserId(userId);

        // userEntity가 존재하면 User로 변환하여 반환
        if (entity.isPresent()) {
            Email email = emailConverter.userEmailVerificationEntityToEmail(entity.get());
            return email;
        }
        return null;

    }

    /**
     * user id로 User 조회
     */
    @Override
    public User findUserByUserId(Long userId) {
        Optional<UserEntity> entity = jpaUserRepository.findById(userId);

        // userEntity가 존재하면 User로 변환하여 반환
        if (entity.isPresent()) {
            User user = userConverter.toDomainBasic(entity.get());
            return user;
        }
        return null;

    }

    /**
     * 사용자 인증정보 업데이트
     */

    @Override
    public void updateEmailVerificationStatus(Long userId, String isEmailVerified) {
        jpaUserRepository.updateEmailVerificationStatus(userId, isEmailVerified);
    }

    /**
     * 이메일 인증 저장
     */

    @Override
    public void saveEmailVerification(Email emailObject) {
        Long userId = emailObject.getUserId();  // userId 가져오기


        Optional<EmailVerificationEntity> emailEntity = jpaUserEmailVerificationRepository.findEmailByUserId(userId);

        // 있으면 업데이트
        emailEntity.ifPresentOrElse(
                entity -> jpaUserEmailVerificationRepository.updateVerificationInfo(
                        entity.getEmailVerificationId(),
                        emailObject.getVerificationCode(),
                        emailObject.getExpiresDate(),
                        emailObject.getUserId()
                ),
                () -> {
                    // 없으면 저장
                    EmailVerificationEntity emailVerificationEntity = emailConverter.EmailToUserEmailVerificationEntity(emailObject, jpaUserRepository.getReferenceById(userId));
                    jpaUserEmailVerificationRepository.save(emailVerificationEntity);
                }
        );
    }

    /**
     * 사용자 정보 변경
     */

    @Override
    public User changeUserInfo(User user) {

        // 사용자 조회
        Optional<UserEntity> userEntityOptional = jpaUserRepository.findById(user.getUserId());

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();  // <== 한 번만 get()

            userEntity.changeUser(
                    user.getName(),
                    user.getNickname(),
                    user.getPhoneNumber(),
                    user.getBirthDate(),
                    user.getGender()
            );

            return user;
        } else {
            return null;
        }
    }

}
