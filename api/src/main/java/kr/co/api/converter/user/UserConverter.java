package kr.co.api.converter.user;

import kr.co.api.adapter.in.dto.user.request.UserEmailRegistrationRequestDto;
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

    public User registerRequestDtoToDomain(UserEmailRegistrationRequestDto dto) {
        return User.createUserByEmail(dto.getEmail(), dto.getName(), dto.getNickname(), dto.getPassword(), dto.getPasswordCheck(), dto.getPhoneNumber(), dto.getBirthDate() ,dto.getGender());
    }

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
                jpaCompanyRepository.getReferenceById(0)
        );
    }


    // 필요 없는 필드는 제외한 변환 메서드
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
                null // company 제외
        );
    }
}

