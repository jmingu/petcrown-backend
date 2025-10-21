package kr.co.common.entity.user;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class UserEntity {
    private Long userId; // 기본 PK

    private String email; // 이메일 (소셜 로그인 시에도 필요)

    private String userUuid; // 외부 노출용

    private String password; // 비밀번호 (소셜 로그인 사용자는 null 가능)

    private Integer roleId; // 사용자 역할 ID (ex. USER, ADMIN)

    private String name; // 사용자 이름

    private String nickname; // 닉네임

    private String phoneNumber; // 핸드폰 번호

    private LocalDate birthDate; // 생년월일

    private String gender; // 성별 (M, F)
    private Double height; // 키
    private Double weight; // 몸무게

    private String loginType; // 로그인 방식 ID (EMAIL, GOOGLE, KAKAO 등)

    private String loginId; // 로그인 ID (소셜로그인: provider에서 제공하는 고유 ID, 일반 회원가입: email과 동일)

    private String isEmailVerified; // 이메일 인증 여부
    private String isPhoneNumberVerified; // 핸드폰 인증 여부

    private Integer companyId; // 사용자가 소속된 조직 ID

    private String description; // 설명

    private String profileImageUrl; // 프로필 이미지 URL (MyBatis 조회 시 file_info 테이블에서 JOIN)

    // BaseEntity 필드들
    private LocalDateTime createDate; // 생성 일자
    private Long createUserId;  // 생성 ID
    private LocalDateTime updateDate; // 업데이트 일자
    private Long updateUserId;  // 수정ID
    private LocalDateTime deleteDate; // 삭제 일자
    private Long deleteUserId;  // 삭제ID


}
