package kr.co.common.entity.vote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VoteFileInfoEntity {

    public Long voteFileId;
    public LocalDateTime createDate;
    public Long createUserId;
    public LocalDateTime updatedDate;
    public Long updateUserId;
    public LocalDateTime deleteDate;
    public Long deleteUserId;
    public String refTable;       // ref_table_enum
    public Long refId;
    public String fileType;       // file_type_enum
    public Integer sortOrder;
    public String fileUrl;
    public Long fileSize;
    public String mimeType;
    public String fileName;
    public String originalFileName;

    // 생성용
    public VoteFileInfoEntity(Long createUserId, Long updateUserId, String refTable, Long refId, String fileType, Integer sortOrder, String fileUrl, Long fileSize, String mimeType, String fileName, String originalFileName) {

        LocalDateTime now = LocalDateTime.now();

        this.createDate = now;
        this.createUserId = createUserId;
        this.updatedDate = now;
        this.updateUserId = updateUserId;
        this.deleteDate = null;
        this.deleteUserId = null;
        this.refTable = refTable;
        this.refId = refId;
        this.fileType = fileType;
        this.sortOrder = sortOrder;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
    }
}
