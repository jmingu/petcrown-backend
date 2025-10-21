package kr.co.api.community.mapper;

import kr.co.api.community.dto.command.CommunityCommentUpdateDto;
import kr.co.common.entity.community.CommunityCommentEntity;
import kr.co.common.entity.community.CommunityCommentQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityCommentMapper {

    void insertComment(CommunityCommentEntity comment);

    CommunityCommentQueryDto selectByCommentId(@Param("commentId") Long commentId);

    List<CommunityCommentQueryDto> selectByPostId(@Param("postId") Long postId);

    List<CommunityCommentQueryDto> selectRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);

    int countByPostId(@Param("postId") Long postId);

    void updateComment(CommunityCommentUpdateDto commentUpdateDto);

    void incrementLikeCount(@Param("commentId") Long commentId);

    void deleteById(@Param("commentId") Long commentId, @Param("deleteUserId") Long deleteUserId);
}
