package kr.co.common.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserVoteCountHistoryEntity {

    private Long historyId;
    private LocalDateTime createDate;
    private Long createUserId;
    private Long userId;
    private Integer changeAmount;
    private Integer beforeCount;
    private Integer afterCount;

    // 투표 카운트 변경용 생성자
    public UserVoteCountHistoryEntity(Long userId, Integer changeAmount, Integer beforeCount, Integer afterCount, Long createUserId) {
        this.userId = userId;
        this.changeAmount = changeAmount;
        this.beforeCount = beforeCount;
        this.afterCount = afterCount;
        this.createDate = LocalDateTime.now();
        this.createUserId = createUserId;
    }
}