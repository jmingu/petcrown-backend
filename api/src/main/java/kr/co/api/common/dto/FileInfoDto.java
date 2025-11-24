package kr.co.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 파일 정보 내부용 DTO
 */
@Getter
@AllArgsConstructor
public class FileInfoDto {

    private final Long fileId;
    private final String refTable;
    private final Long refId;
    private final String fileType;
    private final Integer sortOrder;
    private final String fileUrl;
    private final Long fileSize;
    private final String mimeType;
    private final String fileName;
    private final String originalFileName;
    private final Long createUserId;

    /**
     * 파일 등록용 생성자 (fileId 없음)
     */
    public FileInfoDto(String refTable, Long refId, String fileType, Integer sortOrder,
                      String fileUrl, Long fileSize, String mimeType, String fileName,
                      String originalFileName, Long createUserId) {
        this(null, refTable, refId, fileType, sortOrder, fileUrl, fileSize, mimeType,
             fileName, originalFileName, createUserId);
    }
}
