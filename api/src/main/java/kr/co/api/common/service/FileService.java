package kr.co.api.common.service;

import kr.co.common.enums.FilePathEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;
    
    /**
     * 이미지 파일들 업로드
     */
    public List<String> uploadImageList(String subPath, List<MultipartFile> files) {
        List<String> uploadedPaths = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String uploadedPath = uploadImage(subPath, file);
                if (uploadedPath != null) {
                    uploadedPaths.add(uploadedPath);
                }
            }
        }
        
        return uploadedPaths;
    }
    
    /**
     * 단일 이미지 파일 업로드
     */
    public String uploadImage(String subPath, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        try {
            // 업로드 디렉토리 생성
            String fullPath = uploadPath + File.separator + subPath;
            Path uploadDir = Paths.get(fullPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 파일명 생성 (UUID + 현재시간 + 원본파일명)
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null && originalFileName.contains(".") 
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
            
            String fileName = UUID.randomUUID().toString() + 
                "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
                extension;
            
            // 파일 저장
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // 반환할 경로 (웹에서 접근 가능한 경로)
            String webPath = "/" + subPath + "/" + fileName;
            
            log.info("File uploaded successfully: {}", webPath);
            return webPath;
            
        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            return null;
        }
    }
    
    /**
     * 파일 삭제
     */
    public boolean deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                return false;
            }
            
            // 웹 경로를 실제 파일 경로로 변환
            String actualPath = uploadPath + filePath.replace("/", File.separator);
            Path path = Paths.get(actualPath);
            
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted successfully: {}", filePath);
                return true;
            } else {
                log.warn("File not found for deletion: {}", filePath);
                return false;
            }
            
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            return false;
        }
    }
}