package kr.co.common.entity.user;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import kr.co.common.entity.standard.company.CompanyEntity;
import kr.co.common.entity.standard.logintype.LoginTypeEntity;
import kr.co.common.entity.standard.role.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // protected 생성자 추가
@Getter
@DynamicUpdate
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 기본 PK

    private String email; // 이메일 (소셜 로그인 시에도 필요)

    private String userUuid; // 외부 노출용

    private String password; // 비밀번호 (소셜 로그인 사용자는 null 가능)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")  // 외래키로 Role을 참조
    private RoleEntity role; // 사용자 역할 (ex. USER, ADMIN)

    private String name; // 사용자 이름

    private String nickname; // 닉네임

    private String phoneNumber; // 핸드폰 번호

    private String profileImageUrl; // 프로필 이미지 URL

    private LocalDate birthDate; // 생년월일

    private String gender; // 성별 (M, F)
    private Double height; // 키
    private Double weight; // 몸무게

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_type_id")  // 외래키로 LoginType을 참조
    private LoginTypeEntity loginType; // 로그인 방식 (EMAIL, GOOGLE, KAKAO 등)

    private String loginId; // 로그인 ID (소셜로그인: provider에서 제공하는 고유 ID, 일반 회원가입: email과 동일)

    private String isEmailVerified; // 이메일 인증 여부
    private String isPhoneNumberVerified; // 핸드폰 인증 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")  // 외래키로 Company를 참조
    private CompanyEntity companyEntity; // 사용자가 소속된 조직

    private String description; // 설명



    private UserEntity(Long createUserId, Long updateUserId, String deleteYn, Long userId, String email, String userUuid, String password, RoleEntity role, String name, String nickname, String phoneNumber, String profileImageUrl, LocalDate birthDate, String gender,  Double height,  Double weight, LoginTypeEntity loginType, String loginId, String isEmailVerified, String isPhoneNumberVerified, CompanyEntity companyEntity, String description) {
        super(createUserId, updateUserId, deleteYn);
        this.userId = userId;
        this.email = email;
        this.userUuid = userUuid;
        this.password = password;
        this.role = role;
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.gender = gender;
        this.height = height; // 초기값 설정
        this.weight = weight; // 초기값 설정
        this.loginType = loginType;
        this.loginId = loginId;
        this.isEmailVerified = isEmailVerified;
        this.isPhoneNumberVerified = isPhoneNumberVerified;
        this.companyEntity = companyEntity;
        this.description = description;
    }

    /**
     * 사용자 엔티티 생성용 메서드
     */
    public static UserEntity createUserEntity(Long userId, String email, String userUuid, String password, RoleEntity role, String name, String nickname, String phoneNumber, String profileImageUrl, LocalDate birthDate, String gender,  Double height,  Double weight, LoginTypeEntity loginType, String loginId, String isEmailVerified, String isPhoneNumberVerified, CompanyEntity companyEntity, String description){
        return new UserEntity(userId, userId, "N", userId, email, userUuid, password, role, name, nickname, phoneNumber, profileImageUrl, birthDate, gender, height, weight, loginType, loginId, isEmailVerified, isPhoneNumberVerified, companyEntity, description);
    }


    /**
     * 사용자 정보 변경용 메서드
     */
    public void changeUser(String name, String nickname, String phoneNumber, LocalDate birthDate, String gender) {
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
    }
}
