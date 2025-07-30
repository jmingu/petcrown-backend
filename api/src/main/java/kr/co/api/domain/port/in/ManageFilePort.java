package kr.co.api.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 입력 포트 (Primary Port)
 * 파일 업로드/다운로드 관련 Use Case를 정의
 */
public interface ManageFilePort {
    
    List<String> uploadImages(String filePath, List<MultipartFile> images);
    
    String uploadSingleImage(String filePath, MultipartFile image);
    
    void deleteFile(String filePath);
    
    byte[] downloadFile(String filePath);
}