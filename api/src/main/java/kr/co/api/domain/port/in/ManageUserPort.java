package kr.co.api.domain.port.in;

import kr.co.api.application.dto.user.response.LoginResponseDto;
import kr.co.api.application.dto.user.response.UserInfoResponseDto;
import kr.co.api.domain.model.user.User;

/**
 * 사용자 관리 입력 포트 (Primary Port)
 * 애플리케이션의 Use Case를 정의
 */
public interface ManageUserPort {
    
    void checkEmailDuplicate(String email);
    
    void checkNicknameDuplicate(String nickname);
    
    void sendEmailVerificationCode(String email);
    
    void verifyEmailCode(String email, String code);
    
    void registerUser(String email, String name, String nickname, String password, 
                     String passwordCheck, String phoneNumber, String birthDate, String gender);
    
    LoginResponseDto login(String email, String password);
    
    LoginResponseDto refreshToken(String refreshToken);
    
    UserInfoResponseDto getUserInfo(Long userId);
    
    void updateUserInfo(Long userId, String name, String nickname, String phoneNumber, 
                       String birthDate, String gender);
    
    void deleteUser(Long userId);
}