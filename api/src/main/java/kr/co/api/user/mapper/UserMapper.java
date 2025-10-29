package kr.co.api.user.mapper;

import kr.co.api.user.dto.command.UserDeletionDto;
import kr.co.api.user.dto.command.UserUpdateDto;
import kr.co.common.entity.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    
    /**
     * 사용자 저장
     */
    void insertUser(UserEntity user);

    
    /**
     * 닉네임으로 사용자 존재 여부 확인
     */
    UserEntity selectByNickname(@Param("nickname") String nickname);
    
    /**
     * 이메일로 사용자 조회
     */
    UserEntity selectByEmail(@Param("email") String email);

    /**
     * 인증상태 업데이트
     */
    void updateEmailVerificationStatus(@Param("userId") Long userId);

    /**
     * ID로 사용자 조회
     */
    UserEntity selectByUserId(@Param("userId") Long userId);
    
    /**
     * 사용자 정보 수정
     */
    void updateUserInfo(UserUpdateDto userUpdateDto);
    
    /**
     * 비밀번호 변경
     */
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);

    /**
     * 이메일, 이름으로 사용자 조회 (비밀번호 찾기용)
     */
    UserEntity selectByEmailAndName(@Param("email") String email, @Param("name") String name);

    /**
     * 사용자 삭제
     */
    void deleteById(@Param("userId") Long userId);

    /**
     * 사용자 정보 검증 및 소프트 삭제 (userId, email, name, password 모두 일치해야 삭제)
     */
    int softDeleteUser(UserDeletionDto userDeletionDto);
}