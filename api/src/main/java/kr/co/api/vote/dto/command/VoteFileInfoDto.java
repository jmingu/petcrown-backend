package kr.co.api.vote.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 투표 파일 정보 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class VoteFileInfoDto {

    private final Long voteFileInfoId;
    private final Long voteWeeklyId;
    private final String fileUrl;
    private final Long fileSize;
    private final String mimeType;
    private final String fileName;
    private final String originalFileName;

    /**
     * 등록용 생성자 (voteFileInfoId 없음)
     */
    public VoteFileInfoDto(Long voteWeeklyId, String fileUrl, Long fileSize, String mimeType,
                          String fileName, String originalFileName) {
        this(null, voteWeeklyId, fileUrl, fileSize, mimeType, fileName, originalFileName);
    }
}
