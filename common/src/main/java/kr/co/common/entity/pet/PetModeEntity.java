package kr.co.common.entity.pet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
@NoArgsConstructor(access = AccessLevel.PRIVATE) // protected 생성자 추가
@Getter
@Slf4j

public class PetModeEntity {
    private Integer petModeId;

    // BaseEntity 공통 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;

    private String modeName;

}
