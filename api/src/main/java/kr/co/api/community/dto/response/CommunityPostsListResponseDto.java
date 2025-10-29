package kr.co.api.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 커뮤니티 게시글 목록 응답 DTO (리스트 + 총 개수)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostsListResponseDto {

    private List<CommunityPostListResponseDto> posts;
    private int totalCount;
}
