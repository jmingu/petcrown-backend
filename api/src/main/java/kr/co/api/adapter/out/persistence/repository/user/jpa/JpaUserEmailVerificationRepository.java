package kr.co.api.adapter.out.persistence.repository.user.jpa;

import kr.co.common.entity.user.UserEmailVerificationEntity;
import kr.co.common.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaUserEmailVerificationRepository extends JpaRepository<UserEmailVerificationEntity, Long> {


    /**
     * userId만으로 엔티티 조회하는 쿼리
     */
    @Query("SELECT u FROM UserEmailVerificationEntity u WHERE u.user.userId = :userId")
    Optional<UserEmailVerificationEntity> findByUserId(@Param("userId") Long userId);

    /**
     * 유저 이메일 검증 정보 업데이트
     */
    @Modifying
    @Query("UPDATE UserEmailVerificationEntity u SET u.verificationCode = :verificationCode, u.expiresDate = :expiresDate, u.updatedDate = CURRENT_TIMESTAMP, u.updateUserId = :userId  WHERE u.emailVerificationId = :emailVerificationId")
    void updateVerificationInfo(@Param("emailVerificationId") Long emailVerificationId, @Param("verificationCode")String verificationCode, @Param("expiresDate")LocalDateTime expiresDate, @Param("userId") Long userId);



}
