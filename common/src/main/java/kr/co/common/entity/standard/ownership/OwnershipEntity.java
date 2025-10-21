package kr.co.common.entity.standard.ownership;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OwnershipEntity {
    private Integer ownershipId;

    // BaseEntity 공통 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;

    private String ownershipName;




}
