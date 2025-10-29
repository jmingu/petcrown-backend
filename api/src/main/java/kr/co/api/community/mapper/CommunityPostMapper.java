package kr.co.api.community.mapper;

import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import kr.co.common.entity.community.CommunityPostEntity;
import kr.co.common.entity.community.CommunityPostQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityPostMapper {

    void insertPost(@Param("entity") CommunityPostEntity post);

    CommunityPostQueryDto selectByPostId(@Param("postId") Long postId);

    List<CommunityPostQueryDto> selectAllPosts(@Param("offset") int offset, @Param("limit") int limit);

    List<CommunityPostQueryDto> selectPinnedPosts();

    int countAllPosts();

    void updatePost(CommunityPostUpdateDto postUpdateDto);

    void incrementViewCount(@Param("postId") Long postId);

    void incrementLikeCount(@Param("postId") Long postId);

    void incrementCommentCount(@Param("postId") Long postId);

    void decrementCommentCount(@Param("postId") Long postId);

    void deleteById(@Param("postId") Long postId, @Param("deleteUserId") Long deleteUserId);

    List<CommunityPostQueryDto> searchByTitle(@Param("title") String title, @Param("offset") int offset, @Param("limit") int limit);

    int countSearchByTitle(@Param("title") String title);
}
