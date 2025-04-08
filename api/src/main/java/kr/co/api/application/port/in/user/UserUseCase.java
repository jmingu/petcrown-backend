package kr.co.api.application.port.in.user;

import kr.co.api.application.dto.user.response.LoginResponseDto;
import kr.co.api.application.dto.user.response.UserInfoResponseDto;
import kr.co.api.domain.model.user.User;

public interface UserUseCase {

    /**
     * 이메일 중복검사
     */
    void findEmail(String email);

    /**
     * 회원가입
     */
    void saveUser(User user);

    /**
     * 사용자 정보 조회
     */
    UserInfoResponseDto findUser(Long userId);

    /**
     * 이메일 인증코드 인증
     */
    void checkEmailCode(String code, String email);

    /**
     * 인증코드 발송
     */
    void sendVerificationCode(String email);

    /**
     * 이메일 로그인
     */
    LoginResponseDto login(String email, String password) throws Exception;

    /**
     * 리프래쉬 토큰으로 토큰 연장
     */
    LoginResponseDto refreshToken(String refreshToken) throws Exception;



}
