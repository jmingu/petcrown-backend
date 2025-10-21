package kr.co.common.entity.vote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VoteMonthlyEntity {
    private Long voteMonthlyId; // 기본 PK

    // BaseEntity 공통 필드들
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private LocalDateTime deleteDate;
    private Long deleteUserId;

    private LocalDate monthStartDate;
    private Long petId;
    private int voteCount;
    private int viewCount;
    private int modeId;

    // 생성용
    public VoteMonthlyEntity(Long createUserId, Long updateUserId, LocalDate monthStartDate, Long petId, int voteCount, int viewCount, int modeId) {

        LocalDateTime now = LocalDateTime.now();

        this.createDate = now;
        this.createUserId = createUserId;
        this.updateDate = now;
        this.updateUserId = updateUserId;
        this.deleteDate = null;
        this.deleteUserId = null;
        this.monthStartDate = monthStartDate;
        this.petId = petId;
        this.voteCount = voteCount;
        this.viewCount = viewCount;
        this.modeId = modeId;
    }
}
