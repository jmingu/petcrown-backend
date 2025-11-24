package kr.co.api.common.repository;

import kr.co.api.common.dto.FileInfoDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.common.jooq.Tables.*;
import static kr.co.common.jooq.enums.RefTableEnum.*;
import static kr.co.common.jooq.enums.FileTypeEnum.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class FileInfoRepository {

    private final DSLContext dsl;

    /**
     * 파일 정보 저장 (생성된 fileId 반환) - 내부용 Dto 사용
     */
    public Long insertFileInfo(FileInfoDto fileInfo) {
        return dsl.insertInto(FILE_INFO)
                .set(FILE_INFO.REF_TABLE, kr.co.common.jooq.enums.RefTableEnum.valueOf(fileInfo.getRefTable()))
                .set(FILE_INFO.REF_ID, fileInfo.getRefId())
                .set(FILE_INFO.FILE_TYPE, kr.co.common.jooq.enums.FileTypeEnum.valueOf(fileInfo.getFileType()))
                .set(FILE_INFO.SORT_ORDER, fileInfo.getSortOrder())
                .set(FILE_INFO.FILE_URL, fileInfo.getFileUrl())
                .set(FILE_INFO.FILE_SIZE, fileInfo.getFileSize())
                .set(FILE_INFO.MIME_TYPE, fileInfo.getMimeType())
                .set(FILE_INFO.FILE_NAME, fileInfo.getFileName())
                .set(FILE_INFO.ORIGINAL_FILE_NAME, fileInfo.getOriginalFileName())
                .set(FILE_INFO.CREATE_DATE, currentLocalDateTime())
                .set(FILE_INFO.CREATE_USER_ID, fileInfo.getCreateUserId())
                .set(FILE_INFO.UPDATED_DATE, currentLocalDateTime())
                .set(FILE_INFO.UPDATE_USER_ID, fileInfo.getCreateUserId())
                .returningResult(FILE_INFO.FILE_ID)
                .fetchOne()
                .getValue(FILE_INFO.FILE_ID);
    }

    /**
     * 파일 정보 저장 (배치) - 내부용 Dto 사용
     */
    public void insertFileInfoBatch(List<FileInfoDto> fileInfoList) {
        if (fileInfoList == null || fileInfoList.isEmpty()) {
            return;
        }

        var inserts = fileInfoList.stream()
                .map(item -> dsl.insertInto(FILE_INFO)
                        .set(FILE_INFO.REF_TABLE, kr.co.common.jooq.enums.RefTableEnum.valueOf(item.getRefTable()))
                        .set(FILE_INFO.REF_ID, item.getRefId())
                        .set(FILE_INFO.FILE_TYPE, kr.co.common.jooq.enums.FileTypeEnum.valueOf(item.getFileType()))
                        .set(FILE_INFO.SORT_ORDER, item.getSortOrder())
                        .set(FILE_INFO.FILE_URL, item.getFileUrl())
                        .set(FILE_INFO.FILE_SIZE, item.getFileSize())
                        .set(FILE_INFO.MIME_TYPE, item.getMimeType())
                        .set(FILE_INFO.FILE_NAME, item.getFileName())
                        .set(FILE_INFO.ORIGINAL_FILE_NAME, item.getOriginalFileName())
                        .set(FILE_INFO.CREATE_DATE, currentLocalDateTime())
                        .set(FILE_INFO.CREATE_USER_ID, item.getCreateUserId())
                        .set(FILE_INFO.UPDATED_DATE, currentLocalDateTime())
                        .set(FILE_INFO.UPDATE_USER_ID, item.getCreateUserId()))
                .toList();

        dsl.batch(inserts).execute();
    }

    /**
     * 참조 테이블과 참조 ID로 파일 정보 조회 - 내부용 Dto 반환
     */
    public List<FileInfoDto> selectByRefTableAndRefId(String refTable, Long refId) {
        return dsl.select()
                .from(FILE_INFO)
                .where(
                        FILE_INFO.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.valueOf(refTable))
                                .and(FILE_INFO.REF_ID.eq(refId))
                                .and(FILE_INFO.DELETE_DATE.isNull())
                )
                .orderBy(FILE_INFO.FILE_TYPE, FILE_INFO.CREATE_DATE)
                .fetch(this::mapToFileInfoDto);
    }

    /**
     * 파일 ID로 파일 정보 조회 - 내부용 Dto 반환
     */
    public FileInfoDto selectByFileId(Long fileId) {
        return dsl.select()
                .from(FILE_INFO)
                .where(
                        FILE_INFO.FILE_ID.eq(fileId)
                                .and(FILE_INFO.DELETE_DATE.isNull())
                )
                .fetchOne(this::mapToFileInfoDto);
    }

    /**
     * 파일 정보 삭제 (논리 삭제)
     */
    public void deleteById(Long fileId, Long deleteUserId) {
        dsl.update(FILE_INFO)
                .set(FILE_INFO.DELETE_DATE, currentLocalDateTime())
                .set(FILE_INFO.DELETE_USER_ID, deleteUserId)
                .where(FILE_INFO.FILE_ID.eq(fileId))
                .execute();
    }

    /**
     * 참조 테이블과 참조 ID로 파일 정보 모두 삭제 (논리 삭제)
     */
    public void deleteByRefTableAndRefId(String refTable, Long refId, Long deleteUserId) {
        dsl.update(FILE_INFO)
                .set(FILE_INFO.DELETE_DATE, currentLocalDateTime())
                .set(FILE_INFO.DELETE_USER_ID, deleteUserId)
                .where(
                        FILE_INFO.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.valueOf(refTable))
                                .and(FILE_INFO.REF_ID.eq(refId))
                )
                .execute();
    }

    /**
     * 특정 타입의 파일만 삭제 (논리 삭제)
     */
    public void deleteByRefTableAndRefIdAndFileType(String refTable, Long refId, String fileType, Long deleteUserId) {
        dsl.update(FILE_INFO)
                .set(FILE_INFO.DELETE_DATE, currentLocalDateTime())
                .set(FILE_INFO.DELETE_USER_ID, deleteUserId)
                .where(
                        FILE_INFO.REF_TABLE.eq(kr.co.common.jooq.enums.RefTableEnum.valueOf(refTable))
                                .and(FILE_INFO.REF_ID.eq(refId))
                                .and(FILE_INFO.FILE_TYPE.eq(kr.co.common.jooq.enums.FileTypeEnum.valueOf(fileType)))
                )
                .execute();
    }

    /**
     * Record를 FileInfoDto로 변환
     */
    private FileInfoDto mapToFileInfoDto(Record record) {
        if (record == null) {
            return null;
        }

        return new FileInfoDto(
                record.get(FILE_INFO.FILE_ID),
                record.get(FILE_INFO.REF_TABLE) != null ? record.get(FILE_INFO.REF_TABLE).getLiteral() : null,
                record.get(FILE_INFO.REF_ID),
                record.get(FILE_INFO.FILE_TYPE) != null ? record.get(FILE_INFO.FILE_TYPE).getLiteral() : null,
                record.get(FILE_INFO.SORT_ORDER),
                record.get(FILE_INFO.FILE_URL),
                record.get(FILE_INFO.FILE_SIZE),
                record.get(FILE_INFO.MIME_TYPE),
                record.get(FILE_INFO.FILE_NAME),
                record.get(FILE_INFO.ORIGINAL_FILE_NAME),
                record.get(FILE_INFO.CREATE_USER_ID)
        );
    }
}
