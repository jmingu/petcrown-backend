package kr.co.api.community.converter.dtoDomain;

import kr.co.api.community.domain.CommunityPost;
import kr.co.api.community.dto.command.CommunityPostRegistrationDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class CommunityPostDtoDomainConverter {

    public CommunityPost toPostForRegistration(CommunityPostRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        return CommunityPost.createPost(
                dto.getUserId(),
                dto.getCategory(),
                dto.getTitle(),
                dto.getContent(),
                dto.getContentType(),
                dto.getCreateUserId()
        );
    }

    public CommunityPost toPostForUpdate(CommunityPostUpdateDto dto) {
        if (dto == null) {
            return null;
        }

        return CommunityPost.createPost(
                null,  // userId는 수정 시 변경하지 않음
                dto.getCategory(),
                dto.getTitle(),
                dto.getContent(),
                dto.getContentType(),
                dto.getUpdateUserId()
        );
    }
}
