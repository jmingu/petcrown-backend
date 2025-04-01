package kr.co.api.application.port.in.user;

import kr.co.api.application.dto.user.response.LoginResponseDto;
import kr.co.api.domain.model.user.User;

public interface UserUseCase {

    /**
     * 이메일 중복검사
     */
    void checkEmailDuplication(String email);

    /**
     * 회원가입
     */
    void register(User user);

    /**
     * 이메일 인증코드 인증
     */
    void verifyEmailCode(String code, String email);

    /**
     * 인증코드 발송
     */
    void sendVerificationCode(String email);

    /**
     * 이메일 로그인
     */
    LoginResponseDto login(String email, String password) throws Exception;
}
