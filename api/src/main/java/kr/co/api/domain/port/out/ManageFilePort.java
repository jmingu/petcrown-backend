package kr.co.api.domain.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 출력 포트 (Secondary Port)
 * 파일 시스템/클라우드 스토리지와의 통신 인터페이스
 */
public interface ManageFilePort {
    
    List<String> uploadFiles(String basePath, List<MultipartFile> files);
    
    String uploadSingleFile(String basePath, MultipartFile file);
    
    void deleteFile(String filePath);
    
    byte[] downloadFile(String filePath);
    
    boolean fileExists(String filePath);
}