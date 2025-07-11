package kr.co.api.application.service.user;

import kr.co.api.application.dto.user.response.LoginResponseDto;
import kr.co.api.application.dto.user.response.UserInfoResponseDto;
import kr.co.api.application.port.in.user.UserUseCase;
import kr.co.api.application.port.out.repository.user.UserRepositoryPort;
import kr.co.api.application.service.async.AsyncService;
import kr.co.api.common.property.JwtProperty;
import kr.co.api.common.util.JwtUtil;
import kr.co.api.domain.model.user.Email;
import kr.co.api.domain.model.user.User;
import kr.co.common.dto.EmailContentDto;
import kr.co.common.enums.BusinessCode;
import kr.co.common.enums.CodeEnum;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.CryptoUtil;
import kr.co.common.util.EmailUtil;
import kr.co.common.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final AsyncService asyncService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperty jwtProperty;
    private final JwtUtil jwtUtil;


    /**
     * 이메일 중복검사
     */
    @Override
    public void findEmail(String email) {

        if(email == null){
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        if(email.trim().isEmpty()){
            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
        }

        // 이메일 유효성 검사
        ValidationUtils.validateEmail(email);
        log.debug("email ==> {}", email);

        // 중복 이메일 검증
        User user  = userRepositoryPort.findByEmail(email);
        log.debug("user ==> {}", user);

        // 이메일이 이미 존재하면 예외 발생
        if (user != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_EMAIL);
        }

    }

    /**
     * 닉네임 중복검사
     */
    @Override
    public void findNickname(String nickname) {

        if(nickname == null){
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        if(nickname.trim().isEmpty()){
            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
        }

        User user = userRepositoryPort.findByNickname(nickname);

        log.debug("user ==> {}", user);

        if (user != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_NICKNAME);
        }
    }

    /**
     * 회원가입
     */
    @Transactional
    @Override
    public void saveUser(User user) {

        // 닉네임 유효성 검사
        User nicknameUser = userRepositoryPort.findByNickname(user.getNickname());
        log.debug("user ==> {}", user);
        if (nicknameUser != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_NICKNAME);
        }

        // 중복 이메일 검증
        User byEmail = userRepositoryPort.findByEmail(user.getEmail());
        // 이메일이 이미 존재하면 예외 발생
        if (byEmail != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        // 인코딩 후 비밀번호 설정
        user.encodedPassword(encodedPassword);

        // 이메일 생성
        Email email = Email.crateEmail(user);

        // 이메일 내용 생성
        EmailContentDto emailContentDto = EmailUtil.generateEmailContent(email.getVerificationCode());

        // 회원가입 및 인증코드 저장
        User registerUser = userRepositoryPort.saveUser(user, email);

        // 이메일 전송(비동기)
        asyncService.sendEmailAsync(registerUser.getEmail(), emailContentDto.getTitle(), emailContentDto.getContent());
    }

    /**
     * 사용자 정보 조회
     */
    @Override
    public UserInfoResponseDto findUser(Long userId) {
        User user = userRepositoryPort.findUserByUserId(userId);
        return new UserInfoResponseDto(user.getEmail(), user.getName(), user.getNickname(), user.getPhoneNumber(), user.getProfileImageUrl(), user.getBirthDate(), user.getGender(), user.getIsEmailVerified());
    }

    /**
     * 이메일 인증코드 인증
     */
    @Transactional
    @Override
    public void checkEmailCode(String code, String email) {

        // 인증코드 검증
        if (code == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 이메일로 사용자 조회
        User user  = userRepositoryPort.findByEmail(email);

        // 사용자 없으면 예외 발생
        if (user == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // 이메일 검증
        Email findEmail = userRepositoryPort.findEmailByUserId(user.getUserId());
        if (findEmail == null) {
            throw new PetCrownException(BusinessCode.EMPTY_VALUE);
        }

        // 만료 시간 확인
        if (findEmail.isExpired()) {
            throw new PetCrownException(BusinessCode.AUTH_CODE_EXPIRED);
        }

        // 인증 코드 확인
        if (!findEmail.isVerificationCodeValid(code)) {
            throw new PetCrownException(BusinessCode.AUTH_CODE_INVALID);
        }

        // 사용자 인증정보 업데이트
        userRepositoryPort.updateEmailVerificationStatus(user.getUserId(), "Y");


    }

    /**
     * 인증코드 발송
     */
    @Transactional
    @Override
    public void sendVerificationCode(String email) {
        // 이메일로 사용자 조회
        User user  = userRepositoryPort.findByEmail(email);

        // 사용자 없으면 예외 발생
        if (user == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // 이메일 인증 정보 생성
        Email emailObject = Email.crateEmail(user);
        // 이메일 내용 생성
        EmailContentDto emailContentDto = EmailUtil.generateEmailContent(emailObject.getVerificationCode());

        // 검증 업데이트/등록
        userRepositoryPort.saveEmailVerification(emailObject);

        // 이메일 전송(비동기)
        asyncService.sendEmailAsync(email, emailContentDto.getTitle(), emailContentDto.getContent());

    }

    /**
     * 로그인
     */
    @Override
    public LoginResponseDto login(String email, String password) throws Exception{
        // 사용자 조회
        // 중복 이메일 검증
        User user  = userRepositoryPort.findByEmail(email);

        // 사용자 존재하지 않으면 값이 없으면 예외를 던지고
        if (user == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // 비밀번호 확인
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD);
        }

        String accessToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(user, jwtProperty.getExpiredTime(), "access"),jwtProperty.getTokenAccessDecryptKey());
        // 리프레시 토큰발행
        String refreshToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(user, jwtProperty.getExpiredRefreshTime(), "refresh"),jwtProperty.getTokenRefreshDecryptKey());

        return new LoginResponseDto(accessToken, refreshToken);
    }

    /**
     * 리프래쉬 토큰으로 엑세스 토큰 연장
     */
    @Override
    public LoginResponseDto refreshToken(String accessToken, String refreshToken) throws Exception {

        // 엑세스 토큰 복호화
        String decryptedAccessToken = CryptoUtil.decrypt(accessToken, jwtProperty.getTokenAccessDecryptKey());

        // 엑세스 토큰 유효 확인(true면 만료)
        if (!jwtUtil.isExpired(decryptedAccessToken, jwtProperty.getSecretKey())) {

            // 엑세스 토큰 만료아니면 에러
            // 만료된 엑세스토큰이여야만 리프래쉬 토큰을 발급할 수 있다.
            throw new PetCrownException(CodeEnum.AUTHENTICATION_ERROR);
        }

        // 리프래쉬 토큰 복호화
        String decryptedRefreshToken = CryptoUtil.decrypt(refreshToken, jwtProperty.getTokenRefreshDecryptKey());

        // 토큰유효 확인
        if (jwtUtil.isExpired(decryptedRefreshToken, jwtProperty.getSecretKey())) {
            //  리프레쉬 만료면 응답번호 440
            throw new PetCrownException(CodeEnum.INVALID_TOKEN);
        }

        // 리프래쉬 토큰인지 확인
        String type = CryptoUtil.decrypt(jwtUtil.getUserName(decryptedRefreshToken, jwtProperty.getSecretKey(), "type"), jwtProperty.getTokenClaimsKey());

        log.debug("type ==> {}", type);
        if (!type.equals("refresh")) {
            throw new PetCrownException(CodeEnum.AUTHENTICATION_ERROR);
        }

        // 토큰의 사용자 아이디 가져오기
        String identifier = jwtUtil.getUserName(decryptedRefreshToken, jwtProperty.getSecretKey(), "identifier");

        log.debug("identifier ==> {}", identifier);
        Long userId = null;
        try {
            userId = Long.parseLong(CryptoUtil.decrypt(identifier, jwtProperty.getTokenClaimsKey()));
        } catch (Exception e) {
            throw new PetCrownException(CodeEnum.AUTHENTICATION_ERROR);
        }

        User user = User.createUserById(userId);

        // 엑세스토큰발행
        String responseAccessToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(user, jwtProperty.getExpiredTime(), "access"),jwtProperty.getTokenAccessDecryptKey());
        // 리프레시 토큰발행
        String responseRefreshToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(user, jwtProperty.getExpiredRefreshTime(), "refresh"),jwtProperty.getTokenRefreshDecryptKey());

        return new LoginResponseDto(responseAccessToken, responseRefreshToken);

    }

    /**
     * 사용자 정보 변경
     */
    @Transactional
    @Override
    public void changeUserInfo(User user) {

        // 유저 아이디가 없으면 예외 발생
        if (user.getUserId() == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // 유저 아이디가 없으면 예외 발생
        User userByUserId = userRepositoryPort.findUserByUserId(user.getUserId());
        if (userByUserId == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // 유저 아이디로 사용자 변경
        userRepositoryPort.changeUserInfo(user);


    }


}
