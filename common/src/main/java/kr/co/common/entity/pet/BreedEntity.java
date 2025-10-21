package kr.co.common.entity.pet;

import java.time.LocalDateTime;
import kr.co.common.entity.standard.species.SpeciesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BreedEntity {
    private Integer breedId;

    // BaseEntity 공통 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;

    private Integer speciesId;

    private String name;




}
