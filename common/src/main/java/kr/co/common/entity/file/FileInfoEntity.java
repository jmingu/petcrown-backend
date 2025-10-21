package kr.co.common.entity.file;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class FileInfoEntity {
    private Long fileId;
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
}
