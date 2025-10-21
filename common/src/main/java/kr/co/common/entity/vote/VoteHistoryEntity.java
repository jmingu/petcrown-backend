package kr.co.common.entity.vote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class VoteHistoryEntity {
    private Long voteHistoryId; // 기본 PK

    private Long userId;
    private Long voteId;

    private LocalDate historyDate;
    private String email;
    private String voteCycle;

    private LocalDateTime createDate;

    // 회원 투표용 생성자(주간)
    public VoteHistoryEntity(Long userId, Long voteId, LocalDate historyDate, String voteCycle) {
        this.userId = userId;
        this.voteId = voteId;
        this.historyDate = historyDate;
        this.createDate = LocalDateTime.now();
        this.voteCycle = voteCycle;
    }

    // 비회원 투표용 생성자
    public VoteHistoryEntity(String email, Long voteId, LocalDate historyDate, String voteCycle) {
        this.email = email;
        this.voteId = voteId;
        this.historyDate = historyDate;
        this.createDate = LocalDateTime.now();
        this.voteCycle = voteCycle;
    }
}
