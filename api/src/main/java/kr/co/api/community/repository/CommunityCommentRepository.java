package kr.co.api.community.repository;

import kr.co.api.community.domain.model.CommunityComment;
import kr.co.api.community.dto.command.CommunityCommentQueryDto;
import kr.co.api.community.dto.command.CommunityCommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class CommunityCommentRepository {

    private final DSLContext dsl;

    /**
     * 댓글 저장 (생성된 commentId 반환)
     */
    public Long insertComment(CommunityComment comment) {
        return dsl.insertInto(COMMUNITY_COMMENT)
                .set(COMMUNITY_COMMENT.POST_ID, comment.getPost().getPostId())
                .set(COMMUNITY_COMMENT.USER_ID, comment.getUser().getUserId())
                .set(COMMUNITY_COMMENT.PARENT_COMMENT_ID, comment.getParentCommentId())
                .set(COMMUNITY_COMMENT.CONTENT, comment.getContent())
                .set(COMMUNITY_COMMENT.LIKE_COUNT, comment.getLikeCount())
                .set(COMMUNITY_COMMENT.DEPTH, comment.getDepth())
                .set(COMMUNITY_COMMENT.CREATE_DATE, currentLocalDateTime())
                .set(COMMUNITY_COMMENT.CREATE_USER_ID, comment.getCreateUser().getUserId())
                .set(COMMUNITY_COMMENT.UPDATE_DATE, currentLocalDateTime())
                .set(COMMUNITY_COMMENT.UPDATE_USER_ID, comment.getCreateUser().getUserId())
                .returningResult(COMMUNITY_COMMENT.COMMENT_ID)
                .fetchOne()
                .getValue(COMMUNITY_COMMENT.COMMENT_ID);
    }

    /**
     * 댓글 ID로 조회
     */
    public CommunityCommentQueryDto selectByCommentId(Long commentId) {
        var cc = COMMUNITY_COMMENT.as("cc");
        var u = USER.as("u");

        return dsl.select()
                .from(cc)
                .leftJoin(u)
                .on(cc.USER_ID.eq(u.USER_ID))
                .where(
                        cc.COMMENT_ID.eq(commentId)
                                .and(cc.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToCommunityCommentQueryDto);
    }

    /**
     * 게시글의 모든 댓글 조회 (최상위 댓글만)
     */
    public List<CommunityCommentQueryDto> selectByPostId(Long postId) {
        var cc = COMMUNITY_COMMENT.as("cc");
        var u = USER.as("u");

        return dsl.select()
                .from(cc)
                .leftJoin(u)
                .on(cc.USER_ID.eq(u.USER_ID))
                .where(
                        cc.POST_ID.eq(postId)
                                .and(cc.PARENT_COMMENT_ID.isNull())
                                .and(cc.DELETE_DATE.isNull())
                )
                .orderBy(cc.CREATE_DATE.asc())
                .fetch(this::mapToCommunityCommentQueryDto);
    }

    /**
     * 특정 댓글의 대댓글 조회
     */
    public List<CommunityCommentQueryDto> selectRepliesByParentCommentId(Long parentCommentId) {
        var cc = COMMUNITY_COMMENT.as("cc");
        var u = USER.as("u");

        return dsl.select()
                .from(cc)
                .leftJoin(u)
                .on(cc.USER_ID.eq(u.USER_ID))
                .where(
                        cc.PARENT_COMMENT_ID.eq(parentCommentId)
                                .and(cc.DELETE_DATE.isNull())
                )
                .orderBy(cc.CREATE_DATE.asc())
                .fetch(this::mapToCommunityCommentQueryDto);
    }

    /**
     * 게시글의 전체 댓글 개수 조회
     */
    public int countByPostId(Long postId) {
        Integer count = dsl.selectCount()
                .from(COMMUNITY_COMMENT)
                .where(
                        COMMUNITY_COMMENT.POST_ID.eq(postId)
                                .and(COMMUNITY_COMMENT.DELETE_DATE.isNull())
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 댓글 수정
     */
    public void updateComment(CommunityCommentUpdateDto commentUpdateDto) {
        dsl.update(COMMUNITY_COMMENT)
                .set(COMMUNITY_COMMENT.CONTENT, commentUpdateDto.getContent())
                .set(COMMUNITY_COMMENT.UPDATE_DATE, currentLocalDateTime())
                .set(COMMUNITY_COMMENT.UPDATE_USER_ID, commentUpdateDto.getUpdateUserId())
                .where(
                        COMMUNITY_COMMENT.COMMENT_ID.eq(commentUpdateDto.getCommentId())
                                .and(COMMUNITY_COMMENT.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 댓글 좋아요 수 증가
     */
    public void incrementLikeCount(Long commentId) {
        dsl.update(COMMUNITY_COMMENT)
                .set(COMMUNITY_COMMENT.LIKE_COUNT, COMMUNITY_COMMENT.LIKE_COUNT.plus(1))
                .set(COMMUNITY_COMMENT.UPDATE_DATE, currentLocalDateTime())
                .where(
                        COMMUNITY_COMMENT.COMMENT_ID.eq(commentId)
                                .and(COMMUNITY_COMMENT.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 댓글 삭제 (논리 삭제)
     */
    public void deleteById(Long commentId, Long deleteUserId) {
        dsl.update(COMMUNITY_COMMENT)
                .set(COMMUNITY_COMMENT.DELETE_DATE, currentLocalDateTime())
                .set(COMMUNITY_COMMENT.DELETE_USER_ID, deleteUserId)
                .where(COMMUNITY_COMMENT.COMMENT_ID.eq(commentId))
                .execute();
    }

    /**
     * 특정 부모 댓글의 모든 대댓글 삭제 (논리 삭제)
     */
    public void deleteRepliesByParentCommentId(Long parentCommentId, Long deleteUserId) {
        dsl.update(COMMUNITY_COMMENT)
                .set(COMMUNITY_COMMENT.DELETE_DATE, currentLocalDateTime())
                .set(COMMUNITY_COMMENT.DELETE_USER_ID, deleteUserId)
                .where(COMMUNITY_COMMENT.PARENT_COMMENT_ID.eq(parentCommentId))
                .execute();
    }

    /**
     * 특정 부모 댓글의 삭제되지 않은 대댓글 개수 조회
     */
    public int countRepliesByParentCommentId(Long parentCommentId) {
        Integer count = dsl.selectCount()
                .from(COMMUNITY_COMMENT)
                .where(
                        COMMUNITY_COMMENT.PARENT_COMMENT_ID.eq(parentCommentId)
                                .and(COMMUNITY_COMMENT.DELETE_DATE.isNull())
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * Record를 CommunityCommentQueryDto로 변환
     */
    private CommunityCommentQueryDto mapToCommunityCommentQueryDto(Record record) {
        if (record == null) {
            return null;
        }

        return new CommunityCommentQueryDto(
                record.get(COMMUNITY_COMMENT.COMMENT_ID),
                record.get(COMMUNITY_COMMENT.POST_ID),
                record.get(COMMUNITY_COMMENT.USER_ID),
                record.get(USER.NICKNAME),
                record.get(COMMUNITY_COMMENT.PARENT_COMMENT_ID),
                record.get(COMMUNITY_COMMENT.CONTENT),
                record.get(COMMUNITY_COMMENT.LIKE_COUNT),
                record.get(COMMUNITY_COMMENT.DEPTH),
                record.get(COMMUNITY_COMMENT.CREATE_DATE),
                record.get(COMMUNITY_COMMENT.CREATE_USER_ID),
                record.get(COMMUNITY_COMMENT.UPDATE_DATE),
                record.get(COMMUNITY_COMMENT.UPDATE_USER_ID),
                record.get(COMMUNITY_COMMENT.DELETE_DATE),
                record.get(COMMUNITY_COMMENT.DELETE_USER_ID)
        );
    }
}
