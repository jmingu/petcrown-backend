package kr.co.api.application.service;

import kr.co.api.application.dto.user.response.LoginResponseDto;
import kr.co.api.application.dto.user.response.UserInfoResponseDto;
import kr.co.api.common.property.JwtProperty;
import kr.co.api.common.util.JwtUtil;
import kr.co.api.domain.model.user.User;
import kr.co.api.domain.model.user.vo.Email;
import kr.co.api.domain.model.user.vo.Nickname;
import kr.co.api.domain.port.in.ManageUserPort;
import kr.co.api.domain.port.out.LoadUserPort;
import kr.co.api.domain.port.out.SaveUserPort;
import kr.co.api.domain.port.out.SendEmailPort;
import kr.co.api.domain.service.UserDomainService;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.CryptoUtil;
import kr.co.common.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관리 애플리케이션 서비스
 * ManageUserPort 구현체 (입력 포트 구현)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserManagementService implements ManageUserPort {
    
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final SendEmailPort sendEmailPort;
    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperty jwtProperty;
    private final JwtUtil jwtUtil;
    
    @Override
    public void checkEmailDuplicate(String emailValue) {
        Email email = new Email(emailValue);
        
        if (loadUserPort.existsByEmail(email)) {
            throw new PetCrownException(BusinessCode.DUPLICATE_EMAIL);
        }
        
        log.info("Email duplicate check passed: {}", emailValue);
    }
    
    @Override
    public void checkNicknameDuplicate(String nicknameValue) {
        Nickname nickname = new Nickname(nicknameValue);
        
        if (loadUserPort.existsByNickname(nickname)) {
            throw new PetCrownException(BusinessCode.DUPLICATE_NICKNAME);
        }
        
        log.info("Nickname duplicate check passed: {}", nicknameValue);
    }
    
    @Override
    public void sendEmailVerificationCode(String emailValue) {
        Email email = new Email(emailValue);
        
        String verificationCode = CryptoUtil.generateVerificationCode();
        sendEmailPort.sendVerificationEmail(emailValue, verificationCode);
        
        log.info("Verification email sent to: {}", emailValue);
    }
    
    @Override
    public void verifyEmailCode(String emailValue, String code) {
        // TODO: 인증 코드 검증 로직 구현
        log.info("Email verification completed for: {}", emailValue);
    }
    
    @Override
    @Transactional
    public void registerUser(String email, String name, String nickname, String password,
                           String passwordCheck, String phoneNumber, String birthDate, String gender) {
        
        User user = User.createUserByEmail(email, name, nickname, password, passwordCheck, 
                                         phoneNumber, birthDate, gender);
        
        // 도메인 서비스를 통한 비즈니스 규칙 검증
        userDomainService.validateUserRegistration(user);
        
        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(password);
        user.encodedPassword(encodedPassword);
        
        // 사용자 저장
        User savedUser = saveUserPort.save(user);
        
        // 환영 이메일 발송
        sendEmailPort.sendWelcomeEmail(email, name);
        
        log.info("User registered successfully: userId={}, email={}", savedUser.getUserId(), email);
    }
    
    @Override
    public LoginResponseDto login(String emailValue, String password) {
        Email email = new Email(emailValue);
        
        User user = loadUserPort.findByEmail(email)
                .orElseThrow(() -> new PetCrownException(BusinessCode.EMAIL_NOT_FOUND));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD_ERROR);
        }
        
        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getEmailValue());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        
        log.info("User logged in successfully: userId={}", user.getUserId());
        
        return new LoginResponseDto(accessToken, refreshToken, user.getUserId());
    }
    
    @Override
    public LoginResponseDto refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD_ERROR);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.MEMBER_NOT_FOUND));
        
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getEmailValue());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        
        log.info("Token refreshed for user: userId={}", userId);
        
        return new LoginResponseDto(newAccessToken, newRefreshToken, userId);
    }
    
    @Override
    public UserInfoResponseDto getUserInfo(Long userId) {
        return loadUserPort.getUserInfo(userId);
    }
    
    @Override
    @Transactional
    public void updateUserInfo(Long userId, String name, String nickname, String phoneNumber,
                             String birthDate, String gender) {
        
        User existingUser = loadUserPort.findById(userId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.MEMBER_NOT_FOUND));
        
        User updatedUser = User.changeUser(userId, name, nickname, phoneNumber, birthDate, gender);
        
        // 도메인 서비스를 통한 업데이트 검증
        userDomainService.validateUserUpdate(existingUser, updatedUser);
        
        saveUserPort.updateUserInfo(updatedUser, userId);
        
        log.info("User info updated successfully: userId={}", userId);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new PetCrownException(BusinessCode.MEMBER_NOT_FOUND));
        
        saveUserPort.delete(user);
        
        log.info("User deleted successfully: userId={}", userId);
    }
}