package kr.co.api.adapter.out.persistence.repository.user.jpa;

import kr.co.common.entity.user.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaUserEmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {


    /**
     * userId만으로 엔티티 조회하는 쿼리
     */
    @Query("SELECT u FROM EmailVerificationEntity u WHERE u.user.userId = :userId")
    Optional<EmailVerificationEntity> findEmailByUserId(@Param("userId") Long userId);

    /**
     * 유저 이메일 검증 정보 업데이트
     */
    @Modifying
    @Query("UPDATE EmailVerificationEntity u SET u.verificationCode = :verificationCode, u.expiresDate = :expiresDate, u.updatedDate = CURRENT_TIMESTAMP, u.updateUserId = :userId  WHERE u.emailVerificationId = :emailVerificationId")
    void updateVerificationInfo(@Param("emailVerificationId") Long emailVerificationId, @Param("verificationCode")String verificationCode, @Param("expiresDate")LocalDateTime expiresDate, @Param("userId") Long userId);


}
