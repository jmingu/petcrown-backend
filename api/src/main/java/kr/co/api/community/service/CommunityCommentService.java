package kr.co.api.community.service;

import kr.co.api.community.domain.CommunityComment;
import kr.co.api.community.dto.command.CommunityCommentInfoDto;
import kr.co.api.community.dto.command.CommunityCommentRegistrationDto;
import kr.co.api.community.dto.command.CommunityCommentUpdateDto;
import kr.co.api.community.mapper.CommunityCommentMapper;
import kr.co.api.community.mapper.CommunityPostMapper;
import kr.co.common.entity.community.CommunityCommentEntity;
import kr.co.common.entity.community.CommunityCommentQueryDto;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommunityCommentService {

    private final CommunityCommentMapper commentMapper;
    private final CommunityPostMapper postMapper;

    @Transactional
    public void createComment(CommunityCommentRegistrationDto commentRegistrationDto) {
        CommunityComment comment;

        if (commentRegistrationDto.getParentCommentId() == null) {
            // 최상위 댓글
            comment = CommunityComment.createComment(
                    commentRegistrationDto.getPostId(),
                    commentRegistrationDto.getUserId(),
                    commentRegistrationDto.getContent(),
                    commentRegistrationDto.getCreateUserId()
            );
        } else {
            // 대댓글
            CommunityCommentQueryDto parentComment = commentMapper.selectByCommentId(commentRegistrationDto.getParentCommentId());
            if (parentComment == null) {
                throw new PetCrownException(BusinessCode.COMMENT_NOT_FOUND);
            }

            // 대댓글의 대댓글은 불가
            if (parentComment.getDepth() != null && parentComment.getDepth() >= 1) {
                throw new PetCrownException(BusinessCode.INVALID_COMMENT_DEPTH);
            }

            comment = CommunityComment.createReply(
                    commentRegistrationDto.getPostId(),
                    commentRegistrationDto.getUserId(),
                    commentRegistrationDto.getParentCommentId(),
                    commentRegistrationDto.getContent(),
                    commentRegistrationDto.getCreateUserId()
            );
        }

        CommunityCommentEntity commentEntity = new CommunityCommentEntity(
                null,
                comment.getPostId(),
                comment.getUserId(),
                comment.getParentCommentId(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getDepth(),
                null,
                comment.getCreateUserId(),
                null,
                comment.getCreateUserId(),
                null,
                null
        );

        commentMapper.insertComment(commentEntity);

        // 게시글의 댓글 수 증가
        postMapper.incrementCommentCount(commentRegistrationDto.getPostId());

        log.info("Comment created successfully: commentId={}", commentEntity.getCommentId());
    }

    public List<CommunityCommentInfoDto> getCommentsByPostId(Long postId, Long userId) {
        // 최상위 댓글만 조회
        List<CommunityCommentQueryDto> comments = commentMapper.selectByPostId(postId);

        List<CommunityCommentInfoDto> commentInfoDtos = new ArrayList<>();

        for (CommunityCommentQueryDto comment : comments) {
            // 각 댓글의 대댓글 조회
            List<CommunityCommentQueryDto> replies = commentMapper.selectRepliesByParentCommentId(comment.getCommentId());
            List<CommunityCommentInfoDto> replyInfoDtos = new ArrayList<>();

            for (CommunityCommentQueryDto reply : replies) {
                // 대댓글 작성자 여부 확인
                String replyWriteYn = (userId != null && userId.equals(reply.getUserId())) ? "Y" : "N";

                replyInfoDtos.add(new CommunityCommentInfoDto(
                        reply.getCommentId(),
                        reply.getPostId(),
                        reply.getNickname(),
                        reply.getParentCommentId(),
                        reply.getContent(),
                        reply.getLikeCount(),
                        reply.getDepth(),
                        reply.getCreateDate(),
                        replyWriteYn,
                        null  // 대댓글은 더 이상 하위 댓글이 없음
                ));
            }

            // 댓글 작성자 여부 확인
            String commentWriteYn = (userId != null && userId.equals(comment.getUserId())) ? "Y" : "N";

            commentInfoDtos.add(new CommunityCommentInfoDto(
                    comment.getCommentId(),
                    comment.getPostId(),
                    comment.getNickname(),
                    comment.getParentCommentId(),
                    comment.getContent(),
                    comment.getLikeCount(),
                    comment.getDepth(),
                    comment.getCreateDate(),
                    commentWriteYn,
                    replyInfoDtos
            ));
        }

        return commentInfoDtos;
    }

    @Transactional
    public void updateComment(CommunityCommentUpdateDto commentUpdateDto) {
        CommunityCommentQueryDto existingComment = commentMapper.selectByCommentId(commentUpdateDto.getCommentId());
        if (existingComment == null) {
            throw new PetCrownException(BusinessCode.COMMENT_NOT_FOUND);
        }

        commentMapper.updateComment(commentUpdateDto);
        log.info("Comment updated successfully: commentId={}", commentUpdateDto.getCommentId());
    }

    @Transactional
    public void deleteComment(Long commentId, Long deleteUserId) {
        CommunityCommentQueryDto existingComment = commentMapper.selectByCommentId(commentId);
        if (existingComment == null) {
            throw new PetCrownException(BusinessCode.COMMENT_NOT_FOUND);
        }

        commentMapper.deleteById(commentId, deleteUserId);

        // 게시글의 댓글 수 감소
        postMapper.decrementCommentCount(existingComment.getPostId());

        log.info("Comment deleted successfully: commentId={}", commentId);
    }

    @Transactional
    public void likeComment(Long commentId) {
        CommunityCommentQueryDto existingComment = commentMapper.selectByCommentId(commentId);
        if (existingComment == null) {
            throw new PetCrownException(BusinessCode.COMMENT_NOT_FOUND);
        }

        commentMapper.incrementLikeCount(commentId);
        log.info("Comment liked: commentId={}", commentId);
    }
}
