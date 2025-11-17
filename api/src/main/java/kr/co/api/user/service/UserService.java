package kr.co.api.user.service;

import kr.co.api.common.property.JwtProperty;
import kr.co.api.common.service.EmailService;
import kr.co.api.common.util.JwtUtil;
import kr.co.api.pet.repository.PetRepository;
import kr.co.api.user.domain.model.Company;
import kr.co.api.user.domain.model.EmailVerification;
import kr.co.api.user.domain.model.Role;
import kr.co.api.user.domain.model.User;
import kr.co.api.user.domain.vo.Email;
import kr.co.api.user.domain.vo.Nickname;
import kr.co.api.user.domain.vo.Password;
import kr.co.api.user.dto.command.*;
import kr.co.api.user.repository.*;
import kr.co.common.entity.standard.company.CompanyEntity;
import kr.co.common.entity.standard.role.RoleEntity;
import kr.co.common.entity.user.EmailVerificationEntity;
import kr.co.common.entity.user.UserEntity;
import kr.co.common.entity.user.UserVoteCountEntity;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        UserEntity userEntity = userRepository.selectByEmail(emailObject.getValue());

        if (userEntity != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_EMAIL);
        }
        
        log.debug("Email duplicate check passed: {}", email);
    }
    
    /**
     * 닉네임 중복 확인
     */
    public void checkNicknameDuplicate(String nickname) {

        Nickname nicknameObject = Nickname.of(nickname);
        UserEntity userEntity = userRepository.selectByNickname(nicknameObject.getValue());
        if (userEntity != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_NICKNAME);
        }

        log.debug("Nickname duplicate check passed: {}", nickname);
    }

    /**
     * 회원가입
     */
    @Transactional
    public void createUser(UserRegistrationDto userRegistrationDto) {

        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        User user = User.createUserByEmail(
                userRegistrationDto.getEmail(),
                userRegistrationDto.getName(),
                userRegistrationDto.getNickname(),
                userRegistrationDto.getPassword(),
                userRegistrationDto.getPasswordCheck()
        );

        // 이메일 검증
        UserEntity emailUserEntity = userRepository.selectByEmail(user.getEmail().getValue());
        if (emailUserEntity != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_EMAIL);
        }

        // 닉네임 검증
        UserEntity nicknameUserEntity = userRepository.selectByNickname(user.getNickname().getValue());
        if (nicknameUserEntity != null) {
            throw new PetCrownException(BusinessCode.DUPLICATE_NICKNAME);
        }

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword().getValue());
        User encodedPasswordUser = user.withEncodedPassword(encodedPassword);

        // 사용자 저장
        // 기본 조회
        RoleEntity defaultRole = roleRepository.selectDefaultRole();
        if (defaultRole == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }

        CompanyEntity defaultCompany = companyRepository.selectDefaultCompany();
        if (defaultCompany == null) {
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }

        // Domain → Entity 변환 (생성자 직접 호출)
        UserEntity userEntity = new UserEntity(
                encodedPasswordUser.getUserId(),  // userId
                encodedPasswordUser.getEmail().getValue(),  // email
                encodedPasswordUser.getUserUuid(),  // userUuid
                encodedPasswordUser.getPassword().getValue(),  // password
                defaultRole.getRoleId(),  // roleId
                encodedPasswordUser.getName().getValue(),  // name
                encodedPasswordUser.getNickname().getValue(),  // nickname
                encodedPasswordUser.getPhoneNumber() != null ? encodedPasswordUser.getPhoneNumber().getValue() : null,  // phoneNumber
                encodedPasswordUser.getBirthDate(),  // birthDate
                encodedPasswordUser.getGender() != null ? encodedPasswordUser.getGender().getValue() : null,  // gender
                null,  // height
                null,  // weight
                "EMAIL",  // loginType
                encodedPasswordUser.getEmail().getValue(),  // loginId
                "N",  // isEmailVerified
                "N",  // isPhoneNumberVerified
                defaultCompany.getCompanyId(),  // companyId
                null,  // description
                null,  // profileImageUrl
                LocalDateTime.now(),  // createDate
                encodedPasswordUser.getUserId(),  // createUserId
                LocalDateTime.now(),  // updateDate
                encodedPasswordUser.getUserId(),  // updateUserId
                null,  // deleteDate
                null   // deleteUserId
        );

        Long userId = userRepository.insertUser(userEntity);
        log.debug("Saved userId ==> {}", userId);

        User savedUser = encodedPasswordUser.withUserId(userId);

        // 이메일 인증 코드 생성
        EmailVerification emailVerification = EmailVerification.createForRegistration(savedUser);

        // EmailVerification을 Entity로 변환 (생성자 직접 호출)
        EmailVerificationEntity emailVerificationEntity = new EmailVerificationEntity(
                emailVerification.getEmailVerificationId(),  // emailVerificationId
                emailVerification.getUserId(),  // userId
                emailVerification.getVerificationCode(),  // verificationCode
                emailVerification.getExpiresDate(),  // expiresDate
                LocalDateTime.now(),  // createDate
                savedUser.getUserId(),  // createUserId
                LocalDateTime.now(),  // updateDate
                savedUser.getUserId()  // updateUserId
        );

        // 이메일 인증코드 저장
        emailVerificationRepository.insertEmailVerification(emailVerificationEntity);
        log.debug("Email verification code generated and saved for: {}", user.getEmail().getValue());


        // 사용자 투표 카운트 초기화
        UserVoteCountEntity userVoteCountEntity = new UserVoteCountEntity(savedUser.getUserId());
        userVoteCountRepository.insertUserVoteCount(userVoteCountEntity);

        // 이메일 인증 코드 발송
        emailService.sendVerificationEmailAsync(user.getEmail().getValue(), emailVerification.getVerificationCode());

    }

    /**
     * 이메일 인증 코드 검증
     */
    @Transactional
    public void verifyEmailCode(String email, String code) {

        User user = User.ofEmail(email);
        EmailVerificationCodeDto emailVerificationCodeDto = emailVerificationRepository.selectEmailCodeByEmail(user.getEmail().getValue());

        // 존재하는 이메일인지 검증
        if(emailVerificationCodeDto == null) {
            throw new PetCrownException(EMAIL_NOT_FOUND);
        }

        // 검증완료인지 검증
        if (!emailVerificationCodeDto.getIsEmailVerified().equals("N")) {
            throw new PetCrownException(ALREADY_VERIFIED);
        }

        // 유저 객체 생성
        User withUser = user.withUserId(emailVerificationCodeDto.getUserId());

        // 이메일 검증 객체 생성
        EmailVerification verifacationCode = EmailVerification.createVerifacationCode(emailVerificationCodeDto.getEmailVerificationId(), withUser, emailVerificationCodeDto.getVerificationCode(), emailVerificationCodeDto.getExpiresDate());

        // 코드 검증
        verifacationCode.verifyCode(code);

        // 검증 끝나면 검증 완료로 업데이트
        userRepository.updateEmailVerificationStatus(verifacationCode.getUserId());


    }

    /**
     * 이메일 인증 코드 재발송
     */
    @Transactional
    public void sendEmailVerificationCode(String emailValue) {

        Email email = Email.of(emailValue);

        // 이메일로 가입 사용자인지 이메일로 검증
        UserEntity emailUserEntity = userRepository.selectByEmail(email.getValue());
        if (emailUserEntity == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        User user = User.ofId(emailUserEntity.getUserId());

        // 이메일 인증 코드 생성
        EmailVerification emailVerification = EmailVerification.createForRegistration(user);

        // 이메일 인증코드를 새 인증코드로 수정
        emailVerificationRepository.updateVerificationNewCode(emailVerification.getUserId(), emailVerification.getVerificationCode(), emailVerification.getExpiresDate());

        // 이메일 인증 코드 발송
        emailService.sendVerificationEmailAsync(emailUserEntity.getEmail(), emailVerification.getVerificationCode());
    }

    /**
     * 로그인
     */
    public LoginTokenDto login(String emailValue, String password) throws Exception{

        Email email = Email.of(emailValue);

        // 이메일로 가입 사용자인지 이메일로 검증
        UserEntity emailUserEntity = userRepository.selectByEmail(email.getValue());
        if (emailUserEntity == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // Entity → Domain 변환 (정적 팩토리 메서드 사용)
        User userDomain = User.getUserAllFiled(
                emailUserEntity.getUserId(),
                Email.of(emailUserEntity.getEmail()),
                emailUserEntity.getUserUuid(),
                kr.co.api.user.domain.vo.UserName.of(emailUserEntity.getName()),
                Nickname.of(emailUserEntity.getNickname()),
                Password.of(emailUserEntity.getPassword()),
                Role.ofId(emailUserEntity.getRoleId()),  // role
                emailUserEntity.getPhoneNumber() != null ? kr.co.api.user.domain.vo.PhoneNumber.of(emailUserEntity.getPhoneNumber()) : null,
                emailUserEntity.getBirthDate(),
                emailUserEntity.getGender() != null ? kr.co.api.user.domain.vo.Gender.of(emailUserEntity.getGender()) : null,
                emailUserEntity.getLoginType(),  // loginType
                emailUserEntity.getLoginId(),
                emailUserEntity.getIsEmailVerified(),
                emailUserEntity.getIsPhoneNumberVerified(),
                Company.ofId(emailUserEntity.getCompanyId()),  // company
                emailUserEntity.getHeight(),
                emailUserEntity.getWeight(),
                emailUserEntity.getDescription()
        );

        // 이메일 검증된 사용자인지 검증
        userDomain.validateEmailVerified();


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

        return new LoginTokenDto(accessToken, refreshToken);
    }

    /**
     * 사용자 정보 조회
     */
    public UserInfoDto getUserInfo(Long userId) {
        UserEntity userEntity = userRepository.selectByUserId(userId);
        if (userEntity == null) {
            throw new PetCrownException(BusinessCode.MEMBER_NOT_FOUND);
        }

        // UserEntity → UserInfoDto 변환 (생성자 직접 호출)
        return new UserInfoDto(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getName(),
                userEntity.getNickname(),
                userEntity.getPhoneNumber(),
                userEntity.getProfileImageUrl(),
                userEntity.getBirthDate(),
                userEntity.getGender(),
                userEntity.getIsEmailVerified()
        );
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



        return new LoginTokenDto(newAccessToken, newRefreshToken);
    }
    


    /**
     * 사용자 정보 수정
     */
    @Transactional
    public void updateUserInfo(UserUpdateDto userUpdateDto) {

        // 기존 사용자 조회
        UserEntity existingUser = userRepository.selectByUserId(userUpdateDto.getUserId());
        if (existingUser == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 닉네임 변경시 중복 검사 (기존 닉네임과 다른 경우에만)
        if (!existingUser.getNickname().equals(userUpdateDto.getNickname())) {
            UserEntity duplicateUser = userRepository.selectByNickname(userUpdateDto.getNickname());
            if (duplicateUser != null) {
                throw new PetCrownException(DUPLICATE_NICKNAME);
            }
        }

        // 사용자 정보 업데이트
        userRepository.updateUserInfo(userUpdateDto);

        log.info("User info updated successfully: userId={}", userUpdateDto.getUserId());
    }

    /**
     * 로컬 환경인지 확인하는 메서드
     */
    private boolean isLocalEnvironment() {
        // 현재 활성 프로파일이 local이거나 dev인 경우 또는 서버 포트가 8080인 경우
        String[] activeProfiles = org.springframework.core.env.Environment.class.isInstance(this) ? 
            new String[0] : System.getProperty("spring.profiles.active", "").split(",");
        
        for (String profile : activeProfiles) {
            if ("local".equals(profile.trim()) || "dev".equals(profile.trim())) {
                return true;
            }
        }
        
        // 포트 8080으로 실행 중인지 확인
        String serverPort = System.getProperty("server.port", "8080");
        return "8080".equals(serverPort);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {

        // 기존 사용자 조회
        UserEntity existingUser = userRepository.selectByUserId(passwordUpdateDto.getUserId());
        if (existingUser == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 현재 비밀번호 VO 생성 및 검증
        Password currentPassword = Password.of(passwordUpdateDto.getCurrentPassword());
        if (!passwordEncoder.matches(currentPassword.getValue(), existingUser.getPassword())) {
            throw new PetCrownException(INVALID_PASSWORD_ERROR);
        }

        // 새 비밀번호 VO 생성 (비밀번호 확인 포함 - VO에서 검증됨)
        Password newPassword = Password.createPasswordCheck(
                passwordUpdateDto.getNewPassword(), 
                passwordUpdateDto.getNewPasswordConfirm()
        );

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(newPassword.getValue());

        // 비밀번호 업데이트
        userRepository.updatePassword(passwordUpdateDto.getUserId(), encodedNewPassword);

        log.info("Password updated successfully: userId={}", passwordUpdateDto.getUserId());
    }

    /**
     * 비밀번호 찾기 (임시 비밀번호 발급)
     */
    @Transactional
    public void resetPassword(PasswordResetDto passwordResetDto) {

        // 이메일, 이름으로 사용자 조회
        UserEntity userEntity = userRepository.selectByEmailAndName(
                passwordResetDto.getEmail(),
                passwordResetDto.getName()
        );

        // 사용자 정보가 없으면 예외
        if (userEntity == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 임시 비밀번호 생성
        String temporaryPassword = CryptoUtil.generateTemporaryPassword();

        // 임시 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // 비밀번호 업데이트
        userRepository.updatePassword(userEntity.getUserId(), encodedPassword);

        // 임시 비밀번호 이메일 발송
        emailService.sendTemporaryPasswordEmailAsync(userEntity.getEmail(), temporaryPassword);

        log.info("Temporary password issued successfully: email={}", userEntity.getEmail());
    }

    /**
     * 사용자 삭제 (소프트 삭제)
     * userId, email, name, password가 모두 일치해야 삭제 가능
     * 사용자 삭제 시 해당 사용자의 모든 Pet도 소프트 삭제
     */
    @Transactional
    public void deleteUser(UserDeletionDto userDeletionDto) {

        // 기존 사용자 조회
        UserEntity existingUser = userRepository.selectByUserId(userDeletionDto.getUserId());
        if (existingUser == null) {
            throw new PetCrownException(MEMBER_NOT_FOUND);
        }

        // 비밀번호 검증
        Password password = Password.of(userDeletionDto.getPassword());
        if (!passwordEncoder.matches(password.getValue(), existingUser.getPassword())) {
            throw new PetCrownException(INVALID_PASSWORD_ERROR);
        }

        // 1. 사용자의 모든 Pet 소프트 삭제 (단일 쿼리로 처리)
        petRepository.deleteAllPetsByUserId(userDeletionDto.getUserId(), userDeletionDto.getUserId());
        log.info("All pets deleted for user: userId={}", userDeletionDto.getUserId());

        // 2. 암호화된 비밀번호를 dto에 포함시켜 소프트 삭제 수행
        UserDeletionDto deletionDtoWithEncodedPassword = new UserDeletionDto(
                userDeletionDto.getUserId(),
                userDeletionDto.getEmail(),
                userDeletionDto.getName(),
                existingUser.getPassword() // 이미 암호화된 비밀번호
        );

        // 3. userId, email, name, password 모두 일치하는 경우에만 소프트 삭제
        int deletedCount = userRepository.softDeleteUser(deletionDtoWithEncodedPassword);

        if (deletedCount == 0) {
            // 조건이 하나라도 일치하지 않으면 삭제 실패
            throw new PetCrownException(AUTHENTICATION_ERROR);
        }

        log.info("User soft deleted successfully: userId={}, email={}", userDeletionDto.getUserId(), userDeletionDto.getEmail());
    }
//
//    // ========================
//    // 비즈니스 로직 메서드들
//    // ========================
//
//    /**
//     * 사용자 등록 검증
//     */
//    private void validateUserRegistration(User user) {
//        if (user == null) {
//            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
//        }
//
//        // 필수 값 검증 (도메인 객체에서 처리할 수 있는 것들은 도메인에서 처리)
////        user.validateRequiredFields();
//    }
//
//    /**
//     * 사용자 업데이트 검증
//     */
//    private void validateUserUpdate(User existingUser, User updatedUser) {
//        if (existingUser == null || updatedUser == null) {
//            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
//        }
//
//        if (!existingUser.getUserId().equals(updatedUser.getUserId())) {
//            throw new PetCrownException(BusinessCode.INVALID_USER_UPDATE);
//        }
//
//        // 필수 값 검증
////        updatedUser.validateRequiredFields();
//    }
}