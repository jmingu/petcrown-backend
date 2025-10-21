package kr.co.common.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserVoteCountEntity {

    private Long userId;
    private LocalDateTime createDate;
    private Long createUserId;
    private LocalDateTime updateDate;
    private Long updateUserId;
    private Integer voteCount;

    // 회원가입용 생성자
    public UserVoteCountEntity(Long userId) {
        this.userId = userId;
        this.voteCount = 1;
        this.createDate = LocalDateTime.now();
        this.createUserId = userId;
        this.updateDate = LocalDateTime.now();
        this.updateUserId = userId;
    }
}