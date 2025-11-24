package kr.co.api.user.service;

import kr.co.api.common.property.JwtProperty;
import kr.co.api.common.service.EmailService;
import kr.co.api.common.util.JwtUtil;
import kr.co.api.pet.repository.PetRepository;
import kr.co.api.user.domain.model.*;
import kr.co.api.user.domain.vo.Email;
import kr.co.api.user.domain.vo.Nickname;
import kr.co.api.user.domain.vo.Password;
import kr.co.api.user.dto.command.*;
import kr.co.api.user.dto.response.LoginResponseDto;
import kr.co.api.user.dto.response.UserInfoResponseDto;
import kr.co.api.user.repository.*;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.co.common.enums.BusinessCode.*;
import static kr.co.common.enums.CodeEnum.AUTHENTICATION_ERROR;
import static kr.co.common.enums.CodeEnum.INVALID_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperty jwtProperty;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final UserVoteCountRepository userVoteCountRepository;
    private final PetRepository petRepository;

    /**
     * 이메일 중복 확인
     */
    public void checkEmailDuplicate(String email) {

        Email emailObject = Email.of(email);

        UserInfoDto userInfoDto = userRepository.selectByEmail(emailObject.getValue());

        if (userInfoDto != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_EMAIL);
        }
        
        log.debug("Email duplicate check passed: {}", email);
    }
    
    /**
     * 닉네임 중복 확인
     */
    public void checkNicknameDuplicate(String nicknameValue) {

        Nickname nickname =  Nickname.of(nicknameValue);

        UserInfoDto userInfoDto = userRepository.selectByNickname(nickname.getValue());
        if (userInfoDto != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_NICKNAME);
        }

        log.debug("Nickname duplicate check passed: {}", nickname);
    }

    /**
     * 회원가입
     */
    @Transactional
    public void createUser(UserRegistrationDto userRegistrationDto) {
        // 기본 조회
        Role defaultRole = roleRepository.selectDefaultRole();
        if (defaultRole == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }

        Company defaultCompany = companyRepository.selectDefaultCompany();
        if (defaultCompany == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }

        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        User user = User.createUserByEmail(
                userRegistrationDto.getEmail(),
                userRegistrationDto.getName(),
                userRegistrationDto.getNickname(),
                userRegistrationDto.getPassword(),
                userRegistrationDto.getPasswordCheck(),
                defaultRole,
                defaultCompany
        );

        // 이메일 검증
        UserInfoDto userEmailInfoDto = userRepository.selectByEmail(user.getEmail().getValue());
        if (userEmailInfoDto != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_EMAIL);
        }

        // 닉네임 검증
        UserInfoDto userInfoDto = userRepository.selectByNickname(user.getNickname().getValue());
        if (userInfoDto != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_NICKNAME);
        }

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword().getValue());
        User encodedPasswordUser = user.withEncodedPassword(encodedPassword);

        // 사용자 저장
        User saveUser = userRepository.insertUser(encodedPasswordUser);
        log.debug("Saved userId ==> {}", saveUser.getUserId());


        // 이메일 인증 코드 생성
        EmailVerification emailVerification = EmailVerification.createForRegistration(saveUser);


        // 이메일 인증코드 저장
        emailVerificationRepository.insertEmailVerification(emailVerification);
        log.debug("Email verification code generated and saved for: {}", user.getEmail().getValue());


        // 사용자 투표 카운트 초기화
        UserVoteCount userVoteCount = UserVoteCount.createForRegistration(saveUser);
        userVoteCountRepository.insertUserVoteCount(userVoteCount);

        // 이메일 인증 코드 발송
        emailService.sendVerificationEmailAsync(user.getEmail().getValue(), emailVerification.getVerificationCode());

    }

    /**
     * 이메일 인증 코드 검증
     */
    @Transactional
    public void verifyEmailCode(String email, String code) {

        Email emailObject = Email.of(email);
        EmailVerificationCodeDto emailVerificationCodeDto = emailVerificationRepository.selectEmailCodeByEmail(emailObject.getValue());

        // 존재하는 이메일인지 검증
        if(emailVerificationCodeDto == null) {
            throw new PetCrownException(EMAIL_NOT_FOUND);
        }

        // 검증완료인지 검증
        if (!emailVerificationCodeDto.getIsEmailVerified().equals("N")) {
            throw new PetCrownException(ALREADY_VERIFIED);
        }

        // 이메일 검증 객체 생성
        EmailVerification verifacationCode = EmailVerification.createVerifacationCode(emailVerificationCodeDto.getEmailVerificationId(), null, emailVerificationCodeDto.getVerificationCode(), emailVerificationCodeDto.getExpiresDate());

        // 코드 검증
        verifacationCode.verifyCode(code);

        // 검증 끝나면 검증 완료로 업데이트
        userRepository.updateEmailVerificationStatus(emailVerificationCodeDto.getUserId());


    }

    /**
     * 이메일 인증 코드 재발송
     */
    @Transactional
    public void sendEmailVerificationCode(String emailValue) {

        Email email = Email.of(emailValue);

        // 이메일로 가입 사용자인지 이메일로 검증
        UserInfoDto userInfoDto = userRepository.selectByEmail(email.getValue());
        if (userInfoDto == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // 이메일 인증 코드 생성
        EmailVerification emailVerification = EmailVerification.createForRegistration();

        // 이메일 인증코드를 새 인증코드로 수정
        emailVerificationRepository.updateVerificationNewCode(userInfoDto.getUserId(), emailVerification.getVerificationCode(), emailVerification.getExpiresDate());

        // 이메일 인증 코드 발송
        emailService.sendVerificationEmailAsync(userInfoDto.getEmail(), emailVerification.getVerificationCode());
    }

    /**
     * 로그인
     */
    public LoginTokenDto login(String emailValue, String password) throws Exception{

        Email email = Email.of(emailValue);

        // 이메일로 가입 사용자인지 이메일로 검증
        UserInfoDto userInfoDto = userRepository.selectByEmail(email.getValue());
        if (userInfoDto == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // Entity → Domain 변환 (내부에서 이메일 검증 확인)
        User userDomain = User.loginUser(
                userInfoDto.getUserId(),
                userInfoDto.getEmail(),
                userInfoDto.getPassword(),
                userInfoDto.getName(),
                userInfoDto.getNickname(),
                userInfoDto.getPhoneNumber(),
                userInfoDto.getProfileImageUrl(),
                userInfoDto.getBirthDate(),
                userInfoDto.getGender(),
                userInfoDto.getIsEmailVerified()
        );

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, userDomain.getPassword().getValue())) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD_ERROR);
        }

        // JWT 토큰 생성 (localhost는 24시간)
        int accessTokenExpiredTime = jwtProperty.getExpiredTime(); // 기본값 사용
        // 개발환경에서는 24시간으로 설정 (1440분)
//        if (isLocalEnvironment()) {
//            accessTokenExpiredTime = 1440; // 24시간
//        }
        
        String accessToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userDomain.getUserId(), accessTokenExpiredTime, "access"),jwtProperty.getTokenAccessDecryptKey());
        // 리프레시 토큰발행
        String refreshToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userDomain.getUserId(), jwtProperty.getExpiredRefreshTime(), "refresh"),jwtProperty.getTokenRefreshDecryptKey());


        return new LoginTokenDto(
                accessToken,
                refreshToken
        );
    }

    /**
     * 사용자 정보 조회
     */
    public UserInfoDto getUserInfo(Long userId) {
        UserInfoDto userInfoDto = userRepository.selectByUserId(userId);
        if (userInfoDto == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        return userInfoDto;
    }

    /**
     * 토큰 갱신
     */
    public LoginTokenDto refreshToken(String accessToken, String refreshToken) throws Exception{
        // 엑세스 토큰 복호화
        String decryptedAccessToken = CryptoUtil.decrypt(accessToken, jwtProperty.getTokenAccessDecryptKey());

        // 엑세스 토큰 유효 확인(true면 만료)
        if (!jwtUtil.isExpired(decryptedAccessToken, jwtProperty.getSecretKey())) {

            // 엑세스 토큰 만료아니면 에러
            // accessToken이 아직 살아있으면 refresh token을 발급할 수 없음
            // 만료된 엑세스토큰이여야만 리프래쉬 토큰을 발급할 수 있다.
            throw new PetCrownException(AUTHENTICATION_ERROR);
        }

        // 리프래쉬 토큰 복호화
        String decryptedRefreshToken = CryptoUtil.decrypt(refreshToken, jwtProperty.getTokenRefreshDecryptKey());

        // 토큰유효 확인
        if (jwtUtil.isExpired(decryptedRefreshToken, jwtProperty.getSecretKey())) {
            //  리프레쉬 만료면 응답번호 440
            throw new PetCrownException(INVALID_TOKEN);
        }


        // 리프래쉬 토큰인지 확인
        String type = CryptoUtil.decrypt(jwtUtil.getUserName(decryptedRefreshToken, jwtProperty.getSecretKey(), "type"), jwtProperty.getTokenClaimsKey());

        log.debug("type ==> {}", type);
        if (!type.equals("refresh")) {
            throw new PetCrownException(AUTHENTICATION_ERROR);
        }

        // 토큰의 사용자 아이디 가져오기
        String identifier = jwtUtil.getUserName(decryptedRefreshToken, jwtProperty.getSecretKey(), "identifier");

        log.debug("identifier ==> {}", identifier);
        Long userId = null;
        try {
            userId = Long.parseLong(CryptoUtil.decrypt(identifier, jwtProperty.getTokenClaimsKey()));
        } catch (Exception e) {
            throw new PetCrownException(AUTHENTICATION_ERROR);
        }

        // JWT 토큰 생성 (localhost는 24시간)
        int accessTokenExpiredTime = jwtProperty.getExpiredTime(); // 기본값 사용
        // 개발환경에서는 24시간으로 설정 (1440분)
//        if (isLocalEnvironment()) {
//            accessTokenExpiredTime = 1440; // 24시간
//        }
        
        String newAccessToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userId, accessTokenExpiredTime, "access"),jwtProperty.getTokenAccessDecryptKey());
        // 리프레시 토큰발행
        String newRefreshToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userId, jwtProperty.getExpiredRefreshTime(), "refresh"),jwtProperty.getTokenRefreshDecryptKey());


        return new LoginTokenDto(
                newAccessToken,
                newRefreshToken
        );
    }
    


    /**
     * 사용자 정보 수정
     */
    @Transactional
    public void updateUserInfo(UserUpdateDto userUpdateDto) {

        // 기존 사용자 조회
        UserInfoDto existingUser = userRepository.selectByUserId(userUpdateDto.getUserId());
        if (existingUser == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 닉네임 변경시 중복 검사 (기존 닉네임과 다른 경우에만)
        if (!existingUser.getNickname().equals(userUpdateDto.getNickname())) {
            UserInfoDto userInfoDto = userRepository.selectByNickname(userUpdateDto.getNickname());
            if (userInfoDto != null) {
                throw new PetCrownException(DUPLICATE_NICKNAME);
            }
        }

        // 내부용 Dto → Domain 변환 (정적 팩토리 메서드 사용)
        User userToUpdate = User.forUpdate(
                userUpdateDto.getUserId(),
                userUpdateDto.getName(),
                userUpdateDto.getNickname(),
                userUpdateDto.getPhoneNumber(),
                userUpdateDto.getBirthDate(),
                userUpdateDto.getGender()
        );

        // 사용자 정보 업데이트 (도메인으로 전달)
        userRepository.updateUserInfo(userToUpdate);

        log.info("User info updated successfully: userId={}", userUpdateDto.getUserId());
    }


    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {

        // 기존 사용자 조회
        UserInfoDto existingUser = userRepository.selectByUserId(passwordUpdateDto.getUserId());
        if (existingUser == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), existingUser.getPassword())) {
            throw new PetCrownException(INVALID_PASSWORD_ERROR);
        }

        // 새 비밀번호 VO 생성 (비밀번호 확인 포함 - VO에서 검증됨)
        Password newPassword = Password.createPasswordCheck(
                passwordUpdateDto.getNewPassword(),
                passwordUpdateDto.getNewPasswordConfirm()
        );

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(newPassword.getValue());

        // 내부용 Dto → Domain 변환 (정적 팩토리 메서드 사용)
        User userToUpdate = User.forPasswordUpdate(passwordUpdateDto.getUserId(), encodedNewPassword);

        // 비밀번호 업데이트 (도메인으로 전달)
        userRepository.updatePassword(userToUpdate);

        log.info("Password updated successfully: userId={}", passwordUpdateDto.getUserId());
    }

    /**
     * 비밀번호 찾기 (임시 비밀번호 발급)
     */
    @Transactional
    public void resetPassword(PasswordResetDto passwordResetDto) {

        // 이메일, 이름으로 사용자 조회
        UserInfoDto userInfoDto = userRepository.selectByEmailAndName(
                passwordResetDto.getEmail(),
                passwordResetDto.getName()
        );

        // 사용자 정보가 없으면 예외
        if (userInfoDto == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 임시 비밀번호 생성
        String temporaryPassword = CryptoUtil.generateTemporaryPassword();

        // 임시 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // 내부용 Dto → Domain 변환 (정적 팩토리 메서드 사용)
        User userToUpdate = User.forPasswordUpdate(userInfoDto.getUserId(), encodedPassword);

        // 비밀번호 업데이트 (도메인으로 전달)
        userRepository.updatePassword(userToUpdate);

        // 임시 비밀번호 이메일 발송
        emailService.sendTemporaryPasswordEmailAsync(userInfoDto.getEmail(), temporaryPassword);

        log.info("Temporary password issued successfully: email={}", userInfoDto.getEmail());
    }

    /**
     * 사용자 삭제 (소프트 삭제)
     * userId, email, name, password가 모두 일치해야 삭제 가능
     * 사용자 삭제 시 해당 사용자의 모든 Pet도 소프트 삭제
     */
    @Transactional
    public void deleteUser(UserDeletionDto userDeletionDto) {

        // 기존 사용자 조회
        UserInfoDto existingUser = userRepository.selectByUserId(userDeletionDto.getUserId());
        if (existingUser == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(userDeletionDto.getPassword(), existingUser.getPassword())) {
            throw new PetCrownException(INVALID_PASSWORD_ERROR);
        }

        // 1. 사용자의 모든 Pet 소프트 삭제 (단일 쿼리로 처리)
        petRepository.deleteAllPetsByUserId(userDeletionDto.getUserId(), userDeletionDto.getUserId());
        log.info("All pets deleted for user: userId={}", userDeletionDto.getUserId());

        // 2. 내부용 Dto → Domain 변환 (정적 팩토리 메서드 사용, 암호화된 비밀번호 사용)
        User userToDelete = User.forDeletion(
                userDeletionDto.getUserId(),
                userDeletionDto.getEmail(),
                userDeletionDto.getName(),
                existingUser.getPassword() // 이미 암호화된 비밀번호
        );

        // 3. userId, email, name, password 모두 일치하는 경우에만 소프트 삭제 (도메인으로 전달)
        int deletedCount = userRepository.softDeleteUser(userToDelete);

        if (deletedCount == 0) {
            // 조건이 하나라도 일치하지 않으면 삭제 실패
            throw new PetCrownException(AUTHENTICATION_ERROR);
        }

        log.info("User soft deleted successfully: userId={}, email={}", userDeletionDto.getUserId(), userDeletionDto.getEmail());
    }

}