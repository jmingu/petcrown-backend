package kr.co.api.user.domain.model;

import kr.co.api.user.domain.vo.*;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@ToString
public class User {

    private final Long userId;
    private final Email email;
    private final String userUuid;
    private final UserName name;
    private final Nickname nickname;
    private final Password password;
    private final Role role;
    private final PhoneNumber phoneNumber;
    private final LocalDate birthDate;
    private final Gender gender;
    private final String loginType;
    private final String loginId;
    private final String isEmailVerified;
    private final String isPhoneNumberVerified;
    private final Company company;
    private final Double height;
    private final Double weight;
    private final String description;


    /**
     * UserId로 유저 생성
     */
    public static User ofId(Long userId) {
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new User(userId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * email로 유저 생성
     */
    public static User ofEmail(String email) {
        if (email == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        Email emailObject = Email.of(email);
        return new User(null, emailObject, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * 이메일을 통해 회원가입하는 유저 생성
     */
    public static User createUserByEmail(String emailValue, String nameValue, String nicknameValue, String passwordValue, String passwordCheck, String phoneNumberValue, LocalDate birthDate, String genderValue) {
        
        // Value Objects 생성 (유효성 검증 포함)
        Email email = Email.of(emailValue);
        UserName name = UserName.of(nameValue);
        Nickname nickname = Nickname.of(nicknameValue);
        PhoneNumber phoneNumber = PhoneNumber.of(phoneNumberValue);
        Gender gender = Gender.of(genderValue);
        Password password = Password.createPasswordCheck(passwordValue, passwordCheck);
        
        // UUID 생성
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 생년월일 변환
        LocalDate localDate = birthDate;
        
        return new User(null, email, uuid, name, nickname, password, null, phoneNumber, localDate, gender, null, null, "N", "N", null, null, null, null);
    }

    /**
     * 인코딩된 패스워드로 새로운 User 객체 생성
     */
    public User withEncodedPassword(String encodedPassword) {
        if(encodedPassword == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        Password encodedPasswordVo = Password.of(encodedPassword);

        return new User(this.userId, this.email, this.userUuid, this.name, this.nickname, 
                       encodedPasswordVo, this.role, this.phoneNumber,
                       this.birthDate, this.gender, this.loginType, this.loginId, 
                       this.isEmailVerified, this.isPhoneNumberVerified, this.company, 
                       this.height, this.weight, this.description);
    }

    /**
     * UserId로 새로운 User 객체 생성
     */
    public User withUserId(Long userId) {
        if(userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        return new User(userId, this.email, this.userUuid, this.name, this.nickname,
                this.password, this.role, this.phoneNumber,
                this.birthDate, this.gender, this.loginType, this.loginId,
                this.isEmailVerified, this.isPhoneNumberVerified, this.company,
                this.height, this.weight, this.description);
    }

    /**
     * 모든 필드로 생성하는 메서드
     */
    public static User getUserAllFiled(Long userId, Email email, String userUuid, UserName name, Nickname nickname,
                                       Password password, Role role, PhoneNumber phoneNumber, LocalDate birthDate, Gender gender,
                                       String loginType, String loginId, String isEmailVerified, String isPhoneNumberVerified, Company company, Double height, Double weight, String description
    ) {
        return new User(userId, email, userUuid, name, nickname, password, role, phoneNumber,
                birthDate, gender, loginType, loginId, isEmailVerified, isPhoneNumberVerified, company, height, weight, description);

    }

    /**
     * 이메일 검증된 회원인지 확인 (검증되지 않은 경우 예외 발생)
     */
    public void validateEmailVerified() {
        if (!"Y".equals(this.isEmailVerified)) {
            throw new PetCrownException(BusinessCode.NEED_VERIFICATION);
        }
    }






    /**
     * 정보변경 유저 생성
     */
//    public static User changeUser(Long userId, String nameValue, String nicknameValue, String phoneNumberValue, String birthDate, String genderValue) {
//
//        if (userId == null) {
//            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
//        }
//
//        // Value Objects 생성 (유효성 검증 포함)
//        UserName name = new UserName(nameValue);
//        Nickname nickname = new Nickname(nicknameValue);
//        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberValue);
//        Gender gender = new Gender(genderValue);
//
//        // 생년월일 변환
//        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");
//
//        return new User(userId, null, null, name, nickname, null, null, phoneNumber, null, localDate, gender, null, null, "N", "N", null);
//    }


    
//    /**
//     * 사용자 나이 계산
//     */
//    public int getAge() {
//        if (birthDate == null) {
//            return 0;
//        }
//        return Period.between(birthDate, LocalDate.now()).getYears();
//    }
//
//    /**
//     * 성인 여부 확인
//     */
//    public boolean isAdult() {
//        return getAge() >= 18;
//    }
//
//    /**
//     * 이메일 인증 여부 확인
//     */
//    public boolean isEmailVerified() {
//        return "Y".equals(isEmailVerified);
//    }
//
//    /**
//     * 전화번호 인증 여부 확인
//     */
//    public boolean isPhoneNumberVerified() {
//        return "Y".equals(isPhoneNumberVerified);
//    }
//
//    /**
//     * 이메일 인증 완료 처리
//     */
//    public void verifyEmail() {
//        this.isEmailVerified = "Y";
//    }
//
//    /**
//     * 전화번호 인증 완료 처리
//     */
//    public void verifyPhoneNumber() {
//        this.isPhoneNumberVerified = "Y";
//    }
//
//    /**
//     * 사용자 정보 업데이트
//     */
//    public void updateUserInfo(UserName name, Nickname nickname, PhoneNumber phoneNumber, Gender gender, LocalDate birthDate) {
//        this.name = name;
//        this.nickname = nickname;
//        this.phoneNumber = phoneNumber;
//        this.gender = gender;
//        this.birthDate = birthDate;
//    }
    




}
