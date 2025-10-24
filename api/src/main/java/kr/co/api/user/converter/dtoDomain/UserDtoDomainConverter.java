package kr.co.api.user.converter.dtoDomain;

import kr.co.api.user.domain.model.User;
import kr.co.api.user.dto.command.UserRegistrationDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoDomainConverter {
    /**
     * 회원가입용 User 객체 생성
     */
    public User toUserForRegistration(UserRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        // User 객체 생성 - 간단한 회원가입 (이메일, 이름, 닉네임, 비밀번호만)
        return User.createUserByEmail(
                dto.getEmail(),
                dto.getName(),
                dto.getNickname(),
                dto.getPassword(),
                dto.getPasswordCheck()
        );
    }
    
    /**
     * User Domain → UserEntity
     */
//    public UserEntity toUserEntity(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        UserEntity entity = new UserEntity();
//        // 기본적인 매핑만 수행 - 나머지는 JPA에서 자동 처리
//        return entity;
//    }
//
//    /**
//     * UserEntity → User Domain (기본 구현)
//     */
//    public User toUser(UserEntity entity) {
//        if (entity == null) {
//            return null;
//        }
//
//        // 기본 User 객체 반환 (실제 구현 시 User 생성자에 맞게 수정)
//        return null; // TODO: User 생성자가 준비되면 구현
//    }
//
//    /**
//     * User Domain → UserInfoResponseDto
//     */
//    public UserInfoResponseDto toUserInfoResponseDto(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        // 기본 응답 DTO 반환 (필요시 구현)
//        return new UserInfoResponseDto();
//    }
}