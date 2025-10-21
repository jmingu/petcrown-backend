package kr.co.api.community.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommunityPostInfoDto {

    private final Long postId;
    private final String nickname;
    private final String category;
    private final String title;
    private final String content;
    private final String contentType;
    private final Long viewCount;
    private final Long likeCount;
    private final Long commentCount;
    private final String isPinned;
    private final Integer pinOrder;
    private final LocalDateTime createDate;
    private final List<String> imageUrls;
}
