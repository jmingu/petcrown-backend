package kr.co.api.community.converter.entityCommand;

import kr.co.api.community.dto.command.CommunityPostInfoDto;
import kr.co.common.entity.community.CommunityPostQueryDto;
import kr.co.common.entity.file.FileInfoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityPostEntityCommandConverter {

    public CommunityPostInfoDto toPostInfoDto(CommunityPostQueryDto queryDto, List<FileInfoEntity> fileInfoEntities) {
        if (queryDto == null) {
            return null;
        }

        List<String> imageUrls = new ArrayList<>();
        if (fileInfoEntities != null) {
            imageUrls = fileInfoEntities.stream()
                    .filter(file -> "IMAGE".equals(file.getFileType()))
                    .map(FileInfoEntity::getFileUrl)
                    .collect(Collectors.toList());
        }

        return new CommunityPostInfoDto(
                queryDto.getPostId(),
                queryDto.getNickname(),
                queryDto.getCategory(),
                queryDto.getTitle(),
                queryDto.getContent(),
                queryDto.getContentType(),
                queryDto.getViewCount(),
                queryDto.getLikeCount(),
                queryDto.getCommentCount(),
                queryDto.getIsPinned(),
                queryDto.getPinOrder(),
                queryDto.getCreateDate(),
                imageUrls
        );
    }

    public CommunityPostInfoDto toPostInfoDto(CommunityPostQueryDto queryDto) {
        return toPostInfoDto(queryDto, null);
    }

    public List<CommunityPostInfoDto> toPostInfoDtos(List<CommunityPostQueryDto> queryDtos) {
        if (queryDtos == null) {
            return null;
        }

        return queryDtos.stream()
                .map(this::toPostInfoDto)
                .collect(Collectors.toList());
    }
}
