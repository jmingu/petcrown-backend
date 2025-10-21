package kr.co.common.entity.standard.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoleEntity {
    private Integer roleId;  // 기본 PK

    private String roleName; // USER, ADMIN 등

    private Integer level;
    
    private String isDefault; // 기본 역할 여부 (Y/N)

    // BaseEntity 필드들
    private LocalDateTime createDate; // 생성 일자
    private Long createUserId;  // 생성 ID
    private LocalDateTime updateDate; // 업데이트 일자
    private Long updateUserId;  // 수정ID
    private LocalDateTime deleteDate; // 삭제 일자
    private Long deleteUserId;

}
