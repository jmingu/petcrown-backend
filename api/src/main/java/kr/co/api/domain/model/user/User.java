package kr.co.api.domain.model.user;

import kr.co.api.domain.model.standard.company.Company;
import kr.co.api.domain.model.standard.logintype.LoginType;
import kr.co.api.domain.model.standard.role.Role;
import kr.co.api.domain.model.user.vo.*;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class User {

    private Long userId;
    private Email email;
    private String userUuid;
    private UserName name;
    private Nickname nickname;
    private String password;
    private Role role;
    private PhoneNumber phoneNumber;
    private String profileImageUrl;
    private LocalDate birthDate;
    private Gender gender;
    private LoginType loginType;
    private String loginId;
    private String isEmailVerified;
    private String isPhoneNumberVerified;
    private Company company;


    /**
     * UserId로 유저 생성
     */
    public static User createUserById(Long userId) {
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new User(userId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
    /**
     * 이메일을 통해 회원가입하는 유저 생성
     */
    public static User createUserByEmail(String emailValue, String nameValue, String nicknameValue, String password, String passwordCheck, String phoneNumberValue, String birthDate, String genderValue) {
        
        // Value Objects 생성 (유효성 검증 포함)
        Email email = new Email(emailValue);
        UserName name = new UserName(nameValue);
        Nickname nickname = new Nickname(nicknameValue);
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberValue);
        Gender gender = new Gender(genderValue);
        
        // UUID 생성
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        // 패스워드 검증
        validatePassword(password, passwordCheck);
        
        // 생년월일 변환
        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");
        
        return new User(null, email, uuid, name, nickname, password, null, phoneNumber, null, localDate, gender, null, null, "N", "N", null);
    }

    /**
     * 비밀번호 암호화
     */
    public void encodedPassword(String encodedPassword) {
        if(encodedPassword == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        this.password = encodedPassword;
    }
    
    /**
     * 패스워드 검증
     */
    private static void validatePassword(String password, String passwordCheck) {
        if (password == null || password.length() < 4 || password.length() > 15) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD);
        }
        if (passwordCheck == null || passwordCheck.length() < 4 || passwordCheck.length() > 15) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD);
        }
        if (!password.equals(passwordCheck)) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD);
        }
    }

    /**
     * 정보변경 유저 생성
     */
    public static User changeUser(Long userId, String nameValue, String nicknameValue, String phoneNumberValue, String birthDate, String genderValue) {
        
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        
        // Value Objects 생성 (유효성 검증 포함)
        UserName name = new UserName(nameValue);
        Nickname nickname = new Nickname(nicknameValue);
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberValue);
        Gender gender = new Gender(genderValue);
        
        // 생년월일 변환
        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");
        
        return new User(userId, null, null, name, nickname, null, null, phoneNumber, null, localDate, gender, null, null, "N", "N", null);
    }

    /**
     * 모든 필드로 생성하는 메서드
     */
    public static User getUserAllFiled(Long userId, Email email, String userUuid, UserName name, Nickname nickname,
            String password, Role role, PhoneNumber phoneNumber, String profileImageUrl, LocalDate birthDate, Gender gender,
            LoginType loginType, String loginId, String isEmailVerified, String isPhoneNumberVerified, Company company
    ) {
        return new User(userId, email, userUuid, name, nickname, password, role, phoneNumber, profileImageUrl,
                birthDate, gender, loginType, loginId, isEmailVerified, isPhoneNumberVerified, company);
    }
    
    /**
     * 사용자 나이 계산
     */
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    /**
     * 성인 여부 확인
     */
    public boolean isAdult() {
        return getAge() >= 18;
    }
    
    /**
     * 이메일 인증 여부 확인
     */
    public boolean isEmailVerified() {
        return "Y".equals(isEmailVerified);
    }
    
    /**
     * 전화번호 인증 여부 확인
     */
    public boolean isPhoneNumberVerified() {
        return "Y".equals(isPhoneNumberVerified);
    }
    
    /**
     * 이메일 인증 완료 처리
     */
    public void verifyEmail() {
        this.isEmailVerified = "Y";
    }
    
    /**
     * 전화번호 인증 완료 처리
     */
    public void verifyPhoneNumber() {
        this.isPhoneNumberVerified = "Y";
    }
    
    /**
     * 사용자 정보 업데이트
     */
    public void updateUserInfo(UserName name, Nickname nickname, PhoneNumber phoneNumber, Gender gender, LocalDate birthDate) {
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
    }
    
    /**
     * 프로필 이미지 업데이트
     */
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    /**
     * 이메일 값 반환 (기존 호환성을 위해)
     */
    public String getEmailValue() {
        return email != null ? email.getValue() : null;
    }
    
    /**
     * 이름 값 반환 (기존 호환성을 위해)
     */
    public String getNameValue() {
        return name != null ? name.getValue() : null;
    }
    
    /**
     * 닉네임 값 반환 (기존 호환성을 위해)
     */
    public String getNicknameValue() {
        return nickname != null ? nickname.getValue() : null;
    }
    
    /**
     * 전화번호 값 반환 (기존 호환성을 위해)
     */
    public String getPhoneNumberValue() {
        return phoneNumber != null ? phoneNumber.getValue() : null;
    }
    
    /**
     * 성별 값 반환 (기존 호환성을 위해)
     */
    public String getGenderValue() {
        return gender != null ? gender.getCode() : null;
    }



}
