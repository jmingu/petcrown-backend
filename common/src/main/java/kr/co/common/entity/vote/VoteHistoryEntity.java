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

    // 회원 투표용 생성자 (historyDate, createDate는 Repository에서 DB의 current_date/current_timestamp 사용)
    public VoteHistoryEntity(Long userId, Long voteId, String voteCycle) {
        this.userId = userId;
        this.voteId = voteId;
        this.voteCycle = voteCycle;
        // historyDate, createDate는 Repository에서 설정
    }

    // 비회원 투표용 생성자 (historyDate, createDate는 Repository에서 DB의 current_date/current_timestamp 사용)
    public VoteHistoryEntity(String email, Long voteId, String voteCycle) {
        this.email = email;
        this.voteId = voteId;
        this.voteCycle = voteCycle;
        // historyDate, createDate는 Repository에서 설정
    }
}
