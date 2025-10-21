package kr.co.api.user.mapper;

import kr.co.api.user.dto.command.EmailVerificationCodeDto;
import kr.co.common.entity.user.EmailVerificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface EmailVerificationMapper {
    
    /**
     * 이메일 인증 정보 저장
     */
    void insertEmailVerification(EmailVerificationEntity emailVerification);
    
    /**
     * 사용자 Email로 인증코드 조회
     */
    EmailVerificationCodeDto selectEmailCodeByEmail(@Param("email") String email);


    /**
     * 새로운 인증코드로 수정
     */
    void updateVerificationNewCode(@Param("userId")Long userId, @Param("verificationCode")String verificationCode, @Param("expiresDate")LocalDateTime expiresDate);
}