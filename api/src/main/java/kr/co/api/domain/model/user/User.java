package kr.co.api.domain.model.user;

import kr.co.api.domain.model.standard.company.Company;
import kr.co.api.domain.model.standard.logintype.LoginType;
import kr.co.api.domain.model.standard.role.Role;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.util.DateUtils;
import kr.co.common.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class User {
    private Long userId;
    private String email;
    private String userUuid;
    private String name;
    private String nickname;
    private Password password;
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


    // id로 유저 생성
    public User(Long userId) {
        this.userId = userId;
    }

    // 이메일을 통해 가입하는 유저 생성
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
        Password pwd = Password.registerPassword(password, passwordCheck);

        if (!"M".equals(gender) && !"F".equals(gender)) {
            throw new PetCrownException(BusinessCode.GENDER_CHECK_REQUIRED);
        }

        // 생년월인 변환
        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");

        // 도메인 객체로 반환
        return new User(null, email, uuid, name, nickname, pwd, null, phoneNumber, null, localDate ,gender, null, null, "N", "N",null);
    }

    // 패스워드 getter
    public String getPassword() {
        if (password == null) {
            return null;
        }
        return password.getPassword();
    }

    // 비밀번호 일치 확인
    public boolean isPasswordMatching() {
        return this.password.isPasswordMatching();
    }

    // 비밀번호 암호화 후 재 생성
    public User encodePassword(String encodedPassword) {
        return new User(
                this.userId, this.email, this.userUuid, this.name, this.nickname,
                new Password(encodedPassword),  // 암호화된 비밀번호 적용
                this.role, this.phoneNumber, this.profileImageUrl, this.birthDate,
                this.gender, this.loginType, this.loginId, this.isEmailVerified, this.isPhoneNumberVerified, this.company
        );
    }


}
