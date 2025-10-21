package kr.co.api.community.converter.dtoCommand;

import kr.co.api.community.dto.command.CommunityPostInfoDto;
import kr.co.api.community.dto.command.CommunityPostRegistrationDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import kr.co.api.community.dto.request.CommunityPostRegistrationRequestDto;
import kr.co.api.community.dto.request.CommunityPostUpdateRequestDto;
import kr.co.api.community.dto.response.CommunityPostListResponseDto;
import kr.co.api.community.dto.response.CommunityPostResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityPostDtoCommandConverter {

    // Request DTO -> Command DTO

    public CommunityPostRegistrationDto toCommandDto(CommunityPostRegistrationRequestDto request,
                                                     Long userId, Long createUserId) {
        if (request == null) {
            return null;
        }

        return new CommunityPostRegistrationDto(
                userId,
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                createUserId,
                request.getImageFiles()
        );
    }

    public CommunityPostUpdateDto toCommandDto(CommunityPostUpdateRequestDto request, Long updateUserId) {
        if (request == null) {
            return null;
        }

        return new CommunityPostUpdateDto(
                request.getPostId(),
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getContentType(),
                request.getIsPinned(),
                request.getPinOrder(),
                updateUserId,
                request.getImageFiles()
        );
    }

    // Command DTO -> Response DTO

    public CommunityPostResponseDto toResponseDto(CommunityPostInfoDto postInfoDto) {
        if (postInfoDto == null) {
            return null;
        }

        return new CommunityPostResponseDto(
                postInfoDto.getPostId(),
                postInfoDto.getNickname(),
                postInfoDto.getCategory(),
                postInfoDto.getTitle(),
                postInfoDto.getContent(),
                postInfoDto.getContentType(),
                postInfoDto.getViewCount(),
                postInfoDto.getLikeCount(),
                postInfoDto.getCommentCount(),
                postInfoDto.getIsPinned(),
                postInfoDto.getPinOrder(),
                postInfoDto.getCreateDate(),
                postInfoDto.getImageUrls()
        );
    }

    public CommunityPostListResponseDto toListResponseDto(CommunityPostInfoDto postInfoDto) {
        if (postInfoDto == null) {
            return null;
        }

        return new CommunityPostListResponseDto(
                postInfoDto.getPostId(),
                postInfoDto.getNickname(),
                postInfoDto.getCategory(),
                postInfoDto.getTitle(),
                postInfoDto.getViewCount(),
                postInfoDto.getLikeCount(),
                postInfoDto.getCommentCount(),
                postInfoDto.getIsPinned(),
                postInfoDto.getPinOrder(),
                postInfoDto.getCreateDate()
        );
    }

    public List<CommunityPostListResponseDto> toListResponseDtos(List<CommunityPostInfoDto> postInfoDtos) {
        if (postInfoDtos == null) {
            return null;
        }

        return postInfoDtos.stream()
                .map(this::toListResponseDto)
                .collect(Collectors.toList());
    }
}
