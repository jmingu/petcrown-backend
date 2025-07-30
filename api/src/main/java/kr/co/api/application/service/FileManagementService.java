package kr.co.api.application.service;

import kr.co.api.domain.port.in.ManageFilePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 애플리케이션 서비스
 * ManageFilePort 구현체 (입력 포트 구현)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileManagementService implements ManageFilePort {
    
    private final kr.co.api.domain.port.out.ManageFilePort filePort;
    
    @Override
    public List<String> uploadImages(String filePath, List<MultipartFile> images) {
        List<String> uploadedPaths = filePort.uploadFiles(filePath, images);
        
        log.info("Images uploaded successfully: count={}, basePath={}", images.size(), filePath);
        
        return uploadedPaths;
    }
    
    @Override
    public String uploadSingleImage(String filePath, MultipartFile image) {
        String uploadedPath = filePort.uploadSingleFile(filePath, image);
        
        log.info("Single image uploaded successfully: path={}", uploadedPath);
        
        return uploadedPath;
    }
    
    @Override
    public void deleteFile(String filePath) {
        filePort.deleteFile(filePath);
        
        log.info("File deleted successfully: path={}", filePath);
    }
    
    @Override
    public byte[] downloadFile(String filePath) {
        byte[] fileData = filePort.downloadFile(filePath);
        
        log.info("File downloaded successfully: path={}", filePath);
        
        return fileData;
    }
}