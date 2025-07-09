package kr.co.api.domain.model.user;

import kr.co.api.domain.model.standard.company.Company;
import kr.co.api.domain.model.standard.logintype.LoginType;
import kr.co.api.domain.model.standard.role.Role;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.DateUtils;
import kr.co.common.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class User {

    private Long userId;
    private String email;
    private String userUuid;
    private String name;
    private String nickname;
    private String password;
    private Role role; // 사용자 역할
    private String phoneNumber;
    private String profileImageUrl;
    private LocalDate birthDate;
    private String gender;
    private LoginType loginType; // 로그인 타입
    private String loginId;
    private String isEmailVerified;
    private String isPhoneNumberVerified;
    private Company company; // 소속된 회사


    /**
     * UserId로 유저 생성
     */
    public static User createUserById(Long userId) {
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new User(userId, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null);
    }
    /**
     * 이메일을 통해 회원가입하는 유저 생성
     */
    public static User createUserByEmail(String email, String name, String nickname, String password, String passwordCheck, String phoneNumber, String birthDate, String gender) {

        // UUID를 랜덤으로 생성
        UUID random = UUID.randomUUID();
        // UUID에서 하이픈 제거
        String uuid = random.toString().replace("-", "");

        // 핸드폰번호 검증
        String[] phone = phoneNumber.split("-");
        ValidationUtils.validateString(phone[0], 3, 3);
        ValidationUtils.validateString(phone[1], 4, 4);
        ValidationUtils.validateString(phone[2], 4, 4);

        ValidationUtils.validateString(email, 0, 0);
        ValidationUtils.validateEmail(email); // 이메일 패턴 확인
        ValidationUtils.validateString(name, 2, 10);
        ValidationUtils.validateString(nickname, 1, 10);
        ValidationUtils.validateString(birthDate, 8, 8);


        // 성별 검증
        if (!"M".equals(gender) && !"F".equals(gender)) {
            throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
        }

        // 패스워드 검증
        ValidationUtils.validateString(password, 4, 15);
        ValidationUtils.validateString(passwordCheck, 4, 15);
        // 패스워드와 패스워드 체크가 일치하는지 확인
        if(password.equals(passwordCheck) == false) {
            throw new PetCrownException(BusinessCode.INVALID_PASSWORD);
        }

        // 생년월인 변환
        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");

        // 도메인 객체로 반환
        return new User(null, email, uuid, name, nickname, password, null, phoneNumber, null, localDate ,gender, null, null, "N", "N",null);
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
     * 정보변경 유저 생성
     */
    public static User changeUser(Long userId ,String name, String nickname, String phoneNumber, String birthDate, String gender) {

        // 유저 아이디가 없으면 예외 발생
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 핸드폰번호 검증
        String[] phone = phoneNumber.split("-");
        ValidationUtils.validateString(phone[0], 3, 3);
        ValidationUtils.validateString(phone[1], 4, 4);
        ValidationUtils.validateString(phone[2], 4, 4);

        ValidationUtils.validateString(name, 2, 10);
        ValidationUtils.validateString(nickname, 1, 10);
        ValidationUtils.validateString(birthDate, 8, 8);

        // 성별 검증
        if (!"M".equals(gender) && !"F".equals(gender)) {
            throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
        }

        // 생년월인 변환
        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");

        // 도메인 객체로 반환
        return new User(userId, null, null, name, nickname, null, null, phoneNumber, null, localDate ,gender, null, null, "N", "N",null);
    }

    /**
     * 모든 필드로 생성하는 메서드
     */
    public static User getUserAllFiled(Long userId, String email, String userUuid, String name, String nickname,
            String password,Role role, String phoneNumber, String profileImageUrl, LocalDate birthDate, String gender,
            LoginType loginType, String loginId, String isEmailVerified, String isPhoneNumberVerified, Company company
    ) {
        return new User( userId, email,userUuid,name,nickname,password,role,phoneNumber,profileImageUrl,
                birthDate,gender,loginType,loginId,isEmailVerified,isPhoneNumberVerified,company
        );
    }



}
