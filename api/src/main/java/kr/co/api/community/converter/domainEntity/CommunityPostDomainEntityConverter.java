package kr.co.api.community.converter.domainEntity;

import kr.co.api.community.domain.CommunityPost;
import kr.co.api.notice.domain.vo.Content;
import kr.co.api.notice.domain.vo.ContentType;
import kr.co.api.notice.domain.vo.Title;
import kr.co.common.entity.community.CommunityPostEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommunityPostDomainEntityConverter {

    public CommunityPostEntity toPostEntityForRegistration(CommunityPost post) {
        if (post == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();

        return new CommunityPostEntity(
                post.getPostId(),
                post.getUserId(),
                post.getCategory(),
                post.getTitle().getValue(),
                post.getContent().getValue(),
                post.getContentType().getValue(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getIsPinned(),
                post.getPinOrder(),
                now,
                post.getCreateUserId(),
                now,
                post.getCreateUserId(),
                null,
                null
        );
    }

    public CommunityPost toPostDomain(CommunityPostEntity entity) {
        if (entity == null) {
            return null;
        }

        return CommunityPost.getAllFields(
                entity.getPostId(),
                entity.getUserId(),
                entity.getCategory(),
                Title.of(entity.getTitle()),
                Content.of(entity.getContent()),
                ContentType.of(entity.getContentType()),
                entity.getViewCount(),
                entity.getLikeCount(),
                entity.getCommentCount(),
                entity.getIsPinned(),
                entity.getPinOrder(),
                entity.getCreateUserId()
        );
    }
}
