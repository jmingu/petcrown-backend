package kr.co.api.domain.model.user;

import kr.co.common.entity.company.CompanyEntity;
import kr.co.common.entity.user.UserLoginTypeEntity;
import kr.co.common.entity.user.UserRoleEntity;
import kr.co.common.util.DateUtils;
import kr.co.common.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class User {
    private Long userId;
    private String email;
    private String userUuid;
    private String name;
    private String nickname;
    private Password password;
    private UserRoleEntity role; // 사용자 역할
    private String phoneNumber;
    private String profileImageUrl;
    private LocalDate birthDate;
    private String gender;
    private UserLoginTypeEntity loginType; // 로그인 타입
    private String loginId;
    private String isEmailVerified;
    private CompanyEntity company; // 소속된 회사

    public User(String email, String userUuid, String name, String nickname, Password password, UserRoleEntity role, String phoneNumber, String profileImageUrl, LocalDate birthDate, String gender, UserLoginTypeEntity loginType, String loginId, String isEmailVerified, CompanyEntity company) {
        this.email = email;
        this.userUuid = userUuid;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.gender = gender;
        this.loginType = loginType;
        this.loginId = loginId;
        this.isEmailVerified = isEmailVerified;
        this.company = company;
    }

    // 이메일로 유저 생성
    public User(String email) {
        this.email = email;
    }

    // 이메일을 통해 가입하는 유저 생성
    public static User createUserByEmail(String email, String name, String nickname, String password, String passwordCheck, String phoneNumber, String birthDate, String gender) {
        // 기본적인 이메일로 가입한 유저 생성
        // 역할: 0 (일반 사용자)
        UserRoleEntity role = new UserRoleEntity(0, "USER", 0); // 0은 일반 사용자
        // 로그인 방식: 0 (이메일)
        UserLoginTypeEntity loginType = new UserLoginTypeEntity(0, "EMAIL"); // 0은 이메일 로그인
        // 회사: 0 (일반 회사 없음)
        CompanyEntity company = new CompanyEntity(0, "No Company", null, null, null);

        // UUID를 랜덤으로 생성
        UUID random = UUID.randomUUID();
        // UUID에서 하이픈 제거
        String uuid = random.toString().replace("-", "");

        ValidationUtils.validateString(email, 0, 0);
        ValidationUtils.validateEmail(email); // 이메일 패턴 확인
        ValidationUtils.validateString(name, 2, 10);
        ValidationUtils.validateString(nickname, 1, 10);
        ValidationUtils.validateString(birthDate, 8, 8);
        Password pwd = new Password(password, passwordCheck);


        LocalDate localDate = DateUtils.convertToLocalDate(birthDate, "yyyyMMdd");

        // 도메인 객체로 반환
        return new User(email, uuid, name, nickname, pwd, role, phoneNumber, null, localDate ,gender, loginType, null, "N", company);
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



}
