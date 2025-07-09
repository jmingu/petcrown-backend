package kr.co.api.adapter.out.persistence.repository.user.jpa;

import kr.co.common.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * 이메일 중복 검사
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 닉네임 중복 검사
     */
    Optional<UserEntity> findByNickname(String nickname);

    /**
     * userId로 인증정보 업데이트
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.isEmailVerified = :isEmailVerified, u.updateUserId = :userId, u.updatedDate = CURRENT_TIMESTAMP  WHERE u.userId = :userId")
    void updateEmailVerificationStatus(@Param("userId") Long userId, @Param("isEmailVerified") String isEmailVerified);

    /**
     * 사용저 정보 변경
     */
    @Modifying
    @Query("UPDATE UserEntity u " +
            "SET u.name = :name, " +
            "u.nickname = :nickname, " +
            "u.phoneNumber = :phoneNumber, " +
            "u.birthDate = :birthDate, " +
            "u.gender = :gender, " +
            "u.updatedDate = CURRENT_TIMESTAMP, " +
            "u.updateUserId = :userId " +
            "WHERE u.userId = :userId")
    void updateUserInfo(@Param("userId") Long userId, @Param("name") String name, @Param("nickname") String nickname, @Param("phoneNumber") String phoneNumber, @Param("birthDate")LocalDate birthDate, @Param("gender") String gender);
}
