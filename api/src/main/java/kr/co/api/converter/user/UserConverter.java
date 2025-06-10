package kr.co.api.converter.user;

import kr.co.api.adapter.in.dto.user.request.UserEmailRegistrationRequestDto;
import kr.co.api.adapter.in.dto.user.request.UserInfoChangeRequestDto;
import kr.co.api.adapter.out.persistence.repository.standard.company.jpa.JpaCompanyRepository;
import kr.co.api.adapter.out.persistence.repository.standard.logintype.jpa.JpaLoginTypeRepository;
import kr.co.api.adapter.out.persistence.repository.standard.role.jpa.JpaRoleRepository;
import kr.co.api.converter.standard.company.CompanyEntityConverter;
import kr.co.api.converter.standard.logintype.LoginTypeEntityConverter;
import kr.co.api.converter.standard.role.RoleEntityConverter;
import kr.co.api.domain.model.user.Password;
import kr.co.api.domain.model.user.User;
import kr.co.common.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final RoleEntityConverter roleEntityConverter;
    private final LoginTypeEntityConverter loginTypeEntityConverter;
    private final CompanyEntityConverter companyEntityConverter;

    private final JpaRoleRepository jpaRoleRepository;
    private final JpaLoginTypeRepository jpaLoginTypeRepository;
    private final JpaCompanyRepository jpaCompanyRepository;

    /**
     * 회원가입 요청 DTO를 도메인 객체로 변환
     */
    public User registerRequestDtoToDomain(UserEmailRegistrationRequestDto dto) {
        return User.createUserByEmail(dto.getEmail(), dto.getName(), dto.getNickname(), dto.getPassword(), dto.getPasswordCheck(), dto.getPhoneNumber(), dto.getBirthDate() ,dto.getGender());
    }

    /**
     * User 도메인 객체를 UserEntity로 변환
     */
    public UserEntity registerUserToEntity(User user) {

        return new UserEntity(
                user.getUserId(),
                user.getUserId(),
                "N",
                user.getUserId(),
                user.getEmail(),
                user.getUserUuid(),
                user.getPassword(),
                jpaRoleRepository.getReferenceById(0),
                user.getName(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getBirthDate(),
                user.getGender(),
                jpaLoginTypeRepository.getReferenceById(0),
                user.getLoginId(),
                user.getIsEmailVerified(),
                user.getIsPhoneNumberVerified(),
                jpaCompanyRepository.getReferenceById(0)
        );
    }

    /**
     * entity -> User
     * 필요 없는 필드는 제외한 변환 메서드
     */
    public User toDomainBasic(UserEntity userEntity) {
        return new User(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getUserUuid(),
                userEntity.getName(),
                userEntity.getNickname(),
                new Password(userEntity.getPassword()),
                null, // role 제외
                userEntity.getPhoneNumber(),
                userEntity.getProfileImageUrl(),
                userEntity.getBirthDate(),
                userEntity.getGender(),
                null, // loginType 제외
                userEntity.getLoginId(),
                userEntity.getIsEmailVerified(),
                userEntity.getIsPhoneNumberVerified(),
                null // company 제외
        );
    }

    /**
     * 사용자 정보변경 요청 DTO를 도메인 객체로 변환
     */
    public User changeUserRequestDtoToDomain(UserInfoChangeRequestDto dto, Long userId) {
        return User.changeUser(userId, dto.getName(), dto.getNickname(), dto.getPhoneNumber(), dto.getBirthDate(), dto.getGender());

    }

    /**
     * 사용자 정보 변경용
     */
//    public UserEntity changeUserInfoToEntity(User user, UserEntity userEntity) {
//        return new UserEntity(
//                user.getUserId(),
//                user.getEmail(),
//                user.getUserUuid(),
//                user.getPassword() != null ? user.getPassword().getValue() : userEntity.getPassword(), // 비밀번호는 변경된 경우에만 업데이트
//                jpaRoleRepository.getReferenceById(userEntity.getRole().getRoleId()), // 역할은 변경하지 않음
//                user.getName(),
//                user.getNickname(),
//                user.getPhoneNumber(),
//                user.getProfileImageUrl(),
//                user.getBirthDate(),
//                user.getGender(),
//                jpaLoginTypeRepository.getReferenceById(userEntity.getLoginType().getLoginTypeId()), // 로그인 타입은 변경하지 않음
//                user.getLoginId(),
//                userEntity.getIsEmailVerified(), // 이메일 인증 여부는 변경하지 않음
//                userEntity.getIsPhoneNumberVerified(), // 핸드폰 인증 여부는 변경하지 않음
//                jpaCompanyRepository.getReferenceById(userEntity.getCompanyEntity().getCompanyId()) // 회사 정보는 변경하지 않음
//        );
//    }
}

