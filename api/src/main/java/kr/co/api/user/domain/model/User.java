package kr.co.api.user.domain.model;

import kr.co.api.user.domain.vo.*;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.ValidationUtils;
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
     * 이메일을 통해 회원가입하는 유저 생성 (간단한 회원가입: 이메일, 이름, 닉네임, 비밀번호만)
     */
    public static User createUserByEmail(String emailValue, String nameValue, String nicknameValue, String passwordValue, String passwordCheck, Role role, Company company) {

        // Value Objects 생성 (유효성 검증 포함)
        Email email = Email.of(emailValue);
        Password password = Password.createPasswordCheck(passwordValue, passwordCheck);

        UserName userName = UserName.of(nameValue);
        Nickname nickname = Nickname.of(nicknameValue);

        // UUID 생성
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return new User(null, email, uuid, userName, nickname, password, role, null, null, null, null, null, "N", "N", company, null, null, null);
    }

    /**
     * 인코딩된 패스워드로 새로운 User 객체 생성
     */
    public User withEncodedPassword(String encodedPassword) {
        if(encodedPassword == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        Password encodedPasswordVo = Password.from(encodedPassword);

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
     * 이메일 검증된 회원인지 확인 (검증되지 않은 경우 예외 발생)
     */
    public void validateEmailVerified() {
        if (!"Y".equals(this.isEmailVerified)) {
            throw new PetCrownException(BusinessCode.NEED_VERIFICATION);
        }
    }
    /**
     * 로그인 시
     */
    public static User loginUser(Long userId, String email, String password, String nameValue, String nicknameValue, String phoneNumber, String profileImageUrl, LocalDate birthDate, String gender, String isEmailVerified) {

        UserName userName = UserName.from(nameValue);
        Nickname nickname = Nickname.from(nicknameValue);

         User user = new User(userId, Email.from(email), null, userName, nickname, Password.from(password), null, PhoneNumber.of(phoneNumber),
                birthDate, Gender.of(gender), null, null, isEmailVerified, null, null, null, null, null);

         user.validateEmailVerified();

        return user;
    }

    /**
     * 사용자 정보 수정용 (userId, name, nickname, phoneNumber, birthDate, gender)
     */
    public static User forUpdate(Long userId, String nameValue, String nicknameValue, String phoneNumberValue, LocalDate birthDate, String genderValue) {
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        UserName userName = UserName.of(nameValue);
        Nickname nickname = Nickname.of(nicknameValue);
        PhoneNumber phoneNumber = PhoneNumber.of(phoneNumberValue);
        Gender gender = Gender.of(genderValue);

        return new User(userId, null, null, userName, nickname, null, null, phoneNumber, birthDate, gender, null, null, null, null, null, null, null, null);
    }

    /**
     * 비밀번호 수정용
     */
    public static User forPasswordUpdate(Long userId, String encodedPassword) {
        if (userId == null || encodedPassword == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        Password password = Password.from(encodedPassword);

        return new User(userId, null, null, null, null, password, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * 사용자 삭제용 (검증을 위해 userId, email, name, password 필요)
     */
    public static User forDeletion(Long userId, String emailValue, String nameValue, String encodedPassword) {
        if (userId == null || emailValue == null || nameValue == null || encodedPassword == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        Email email = Email.from(emailValue);
        UserName name = UserName.from(nameValue);
        Password password = Password.from(encodedPassword);

        return new User(userId, email, null, name, null, password, null, null, null, null, null, null, null, null, null, null, null, null);
    }

}
