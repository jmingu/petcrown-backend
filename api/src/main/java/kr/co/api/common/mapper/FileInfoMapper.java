package kr.co.api.common.mapper;

import kr.co.common.entity.file.FileInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileInfoMapper {

    /**
     * 파일 정보 저장
     */
    void insertFileInfo(FileInfoEntity fileInfo);

    /**
     * 파일 정보 저장 (배치)
     */
    void insertFileInfoBatch(@Param("fileInfoList") List<FileInfoEntity> fileInfoList);

    /**
     * 참조 테이블과 참조 ID로 파일 정보 조회
     */
    List<FileInfoEntity> selectByRefTableAndRefId(@Param("refTable") String refTable, @Param("refId") Long refId);

    /**
     * 파일 ID로 파일 정보 조회
     */
    FileInfoEntity selectByFileId(@Param("fileId") Long fileId);

    /**
     * 파일 정보 삭제 (논리 삭제)
     */
    void deleteById(@Param("fileId") Long fileId, @Param("deleteUserId") Long deleteUserId);

    /**
     * 참조 테이블과 참조 ID로 파일 정보 모두 삭제 (논리 삭제)
     */
    void deleteByRefTableAndRefId(@Param("refTable") String refTable, @Param("refId") Long refId, @Param("deleteUserId") Long deleteUserId);

    /**
     * 특정 타입의 파일만 삭제 (논리 삭제)
     */
    void deleteByRefTableAndRefIdAndFileType(@Param("refTable") String refTable, @Param("refId") Long refId, @Param("fileType") String fileType, @Param("deleteUserId") Long deleteUserId);
}
