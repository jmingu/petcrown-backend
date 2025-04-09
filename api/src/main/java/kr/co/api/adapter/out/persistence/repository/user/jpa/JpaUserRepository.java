package kr.co.api.adapter.out.persistence.repository.user.jpa;

import kr.co.common.entity.user.UserEmailVerificationEntity;
import kr.co.common.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByNickname(String nickname);

    /**
     * userId로 인증정보 업데이트
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.isEmailVerified = :isEmailVerified, u.updateUserId = :userId, u.updatedDate = CURRENT_TIMESTAMP  WHERE u.userId = :userId")
    void updateEmailVerificationStatus(@Param("userId") Long userId, @Param("isEmailVerified") String isEmailVerified);
}
