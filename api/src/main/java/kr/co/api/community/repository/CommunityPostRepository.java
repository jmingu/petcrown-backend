package kr.co.api.community.repository;

import kr.co.api.community.domain.model.CommunityPost;
import kr.co.api.community.dto.command.CommunityPostQueryDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class CommunityPostRepository {

    private final DSLContext dsl;

    /**
     * 커뮤니티 게시글 저장 (생성된 postId 반환)
     */
    public Long insertPost(CommunityPost post) {
        return dsl.insertInto(COMMUNITY_POST)
                .set(COMMUNITY_POST.USER_ID, post.getUser().getUserId())
                .set(COMMUNITY_POST.CATEGORY, post.getCategory() != null ?
                        kr.co.common.jooq.enums.CommunityCategoryEnum.valueOf(post.getCategory()) : null)
                .set(COMMUNITY_POST.TITLE, post.getTitle() != null ? post.getTitle().getValue() : null)
                .set(COMMUNITY_POST.CONTENT, post.getContent() != null ? post.getContent().getValue() : null)
                .set(COMMUNITY_POST.CONTENT_TYPE, post.getContentType() != null ? post.getContentType().getValue() : null)
                .set(COMMUNITY_POST.VIEW_COUNT, post.getViewCount())
                .set(COMMUNITY_POST.LIKE_COUNT, post.getLikeCount())
                .set(COMMUNITY_POST.COMMENT_COUNT, post.getCommentCount())
                .set(COMMUNITY_POST.IS_PINNED, post.getIsPinned())
                .set(COMMUNITY_POST.PIN_ORDER, post.getPinOrder())
                .set(COMMUNITY_POST.CREATE_DATE, currentLocalDateTime())
                .set(COMMUNITY_POST.CREATE_USER_ID, post.getCreateUser().getUserId())
                .set(COMMUNITY_POST.UPDATE_DATE, currentLocalDateTime())
                .set(COMMUNITY_POST.UPDATE_USER_ID, post.getCreateUser().getUserId())
                .returningResult(COMMUNITY_POST.POST_ID)
                .fetchOne()
                .getValue(COMMUNITY_POST.POST_ID);
    }

    /**
     * 게시글 ID로 조회
     */
    public CommunityPostQueryDto selectByPostId(Long postId) {
        var cp = COMMUNITY_POST.as("cp");
        var u = USER.as("u");

        return dsl.select()
                .from(cp)
                .leftJoin(u)
                .on(cp.USER_ID.eq(u.USER_ID))
                .where(
                        cp.POST_ID.eq(postId)
                                .and(cp.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToCommunityPostQueryDto);
    }

    /**
     * 전체 게시글 목록 조회 (페이징)
     */
    public List<CommunityPostQueryDto> selectAllPosts(int offset, int limit) {
        var cp = COMMUNITY_POST.as("cp");
        var u = USER.as("u");

        return dsl.select()
                .from(cp)
                .leftJoin(u)
                .on(cp.USER_ID.eq(u.USER_ID))
                .where(cp.DELETE_DATE.isNull())
                .orderBy(
                        cp.IS_PINNED.desc(),
                        cp.PIN_ORDER.asc(),
                        cp.CREATE_DATE.desc()
                )
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToCommunityPostQueryDto);
    }

    /**
     * 상단 고정 게시글 목록 조회
     */
    public List<CommunityPostQueryDto> selectPinnedPosts() {
        var cp = COMMUNITY_POST.as("cp");
        var u = USER.as("u");

        return dsl.select()
                .from(cp)
                .leftJoin(u)
                .on(cp.USER_ID.eq(u.USER_ID))
                .where(
                        cp.DELETE_DATE.isNull()
                                .and(cp.IS_PINNED.eq("Y"))
                )
                .orderBy(
                        cp.PIN_ORDER.asc(),
                        cp.CREATE_DATE.desc()
                )
                .fetch(this::mapToCommunityPostQueryDto);
    }

    /**
     * 전체 게시글 개수 조회
     */
    public int countAllPosts() {
        Integer count = dsl.selectCount()
                .from(COMMUNITY_POST)
                .where(COMMUNITY_POST.DELETE_DATE.isNull())
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * 게시글 수정
     */
    public void updatePost(CommunityPostUpdateDto postUpdateDto) {
        dsl.update(COMMUNITY_POST)
                .set(COMMUNITY_POST.CATEGORY, postUpdateDto.getCategory() != null ?
                        kr.co.common.jooq.enums.CommunityCategoryEnum.valueOf(postUpdateDto.getCategory()) : null)
                .set(COMMUNITY_POST.TITLE, postUpdateDto.getTitle())
                .set(COMMUNITY_POST.CONTENT, postUpdateDto.getContent())
                .set(COMMUNITY_POST.CONTENT_TYPE, postUpdateDto.getContentType())
                .set(COMMUNITY_POST.UPDATE_DATE, currentLocalDateTime())
                .set(COMMUNITY_POST.UPDATE_USER_ID, postUpdateDto.getUpdateUserId())
                .where(
                        COMMUNITY_POST.POST_ID.eq(postUpdateDto.getPostId())
                                .and(COMMUNITY_POST.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount(Long postId) {
        dsl.update(COMMUNITY_POST)
                .set(COMMUNITY_POST.VIEW_COUNT, COMMUNITY_POST.VIEW_COUNT.plus(1))
                .set(COMMUNITY_POST.UPDATE_DATE, currentLocalDateTime())
                .where(
                        COMMUNITY_POST.POST_ID.eq(postId)
                                .and(COMMUNITY_POST.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 좋아요 수 증가
     */
    public void incrementLikeCount(Long postId) {
        dsl.update(COMMUNITY_POST)
                .set(COMMUNITY_POST.LIKE_COUNT, COMMUNITY_POST.LIKE_COUNT.plus(1))
                .set(COMMUNITY_POST.UPDATE_DATE, currentLocalDateTime())
                .where(
                        COMMUNITY_POST.POST_ID.eq(postId)
                                .and(COMMUNITY_POST.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 댓글 수 증가
     */
    public void incrementCommentCount(Long postId) {
        dsl.update(COMMUNITY_POST)
                .set(COMMUNITY_POST.COMMENT_COUNT, COMMUNITY_POST.COMMENT_COUNT.plus(1))
                .set(COMMUNITY_POST.UPDATE_DATE, currentLocalDateTime())
                .where(
                        COMMUNITY_POST.POST_ID.eq(postId)
                                .and(COMMUNITY_POST.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 댓글 수 감소
     */
    public void decrementCommentCount(Long postId) {
        dsl.update(COMMUNITY_POST)
                .set(COMMUNITY_POST.COMMENT_COUNT, greatest(COMMUNITY_POST.COMMENT_COUNT.minus(1), inline(0L)))
                .set(COMMUNITY_POST.UPDATE_DATE, currentLocalDateTime())
                .where(
                        COMMUNITY_POST.POST_ID.eq(postId)
                                .and(COMMUNITY_POST.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 댓글 수 직접 업데이트
     */
    public void updateCommentCount(Long postId, int commentCount) {
        dsl.update(COMMUNITY_POST)
                .set(COMMUNITY_POST.COMMENT_COUNT, (long) commentCount)
                .set(COMMUNITY_POST.UPDATE_DATE, currentLocalDateTime())
                .where(
                        COMMUNITY_POST.POST_ID.eq(postId)
                                .and(COMMUNITY_POST.DELETE_DATE.isNull())
                )
                .execute();
    }

    /**
     * 게시글 삭제 (논리 삭제)
     */
    public void deleteById(Long postId, Long deleteUserId) {
        dsl.update(COMMUNITY_POST)
                .set(COMMUNITY_POST.DELETE_DATE, currentLocalDateTime())
                .set(COMMUNITY_POST.DELETE_USER_ID, deleteUserId)
                .where(COMMUNITY_POST.POST_ID.eq(postId))
                .execute();
    }

    /**
     * 제목으로 게시글 검색
     */
    public List<CommunityPostQueryDto> searchByTitle(String title, int offset, int limit) {
        var cp = COMMUNITY_POST.as("cp");
        var u = USER.as("u");

        return dsl.select()
                .from(cp)
                .leftJoin(u)
                .on(cp.USER_ID.eq(u.USER_ID))
                .where(
                        cp.DELETE_DATE.isNull()
                                .and(lower(cp.TITLE).like(lower("%" + title + "%")))
                )
                .orderBy(
                        cp.IS_PINNED.desc(),
                        cp.PIN_ORDER.asc(),
                        cp.CREATE_DATE.desc()
                )
                .offset(offset)
                .limit(limit)
                .fetch(this::mapToCommunityPostQueryDto);
    }

    /**
     * 제목으로 검색된 게시글 개수
     */
    public int countSearchByTitle(String title) {
        Integer count = dsl.selectCount()
                .from(COMMUNITY_POST)
                .where(
                        COMMUNITY_POST.DELETE_DATE.isNull()
                                .and(lower(COMMUNITY_POST.TITLE).like(lower("%" + title + "%")))
                )
                .fetchOne(0, int.class);
        return count != null ? count : 0;
    }

    /**
     * Record를 CommunityPostQueryDto로 변환
     */
    private CommunityPostQueryDto mapToCommunityPostQueryDto(Record record) {
        if (record == null) {
            return null;
        }

        return new CommunityPostQueryDto(
                record.get(COMMUNITY_POST.POST_ID),
                record.get(COMMUNITY_POST.USER_ID),
                record.get(USER.NICKNAME),
                record.get(COMMUNITY_POST.CATEGORY) != null ? record.get(COMMUNITY_POST.CATEGORY).getLiteral() : null,
                record.get(COMMUNITY_POST.TITLE),
                record.get(COMMUNITY_POST.CONTENT),
                record.get(COMMUNITY_POST.CONTENT_TYPE),
                record.get(COMMUNITY_POST.VIEW_COUNT),
                record.get(COMMUNITY_POST.LIKE_COUNT),
                record.get(COMMUNITY_POST.COMMENT_COUNT),
                record.get(COMMUNITY_POST.IS_PINNED),
                record.get(COMMUNITY_POST.PIN_ORDER),
                record.get(COMMUNITY_POST.CREATE_DATE),
                record.get(COMMUNITY_POST.CREATE_USER_ID),
                record.get(COMMUNITY_POST.UPDATE_DATE),
                record.get(COMMUNITY_POST.UPDATE_USER_ID),
                record.get(COMMUNITY_POST.DELETE_DATE),
                record.get(COMMUNITY_POST.DELETE_USER_ID)
        );
    }
}
