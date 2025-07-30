package kr.co.api.adapter.out.file;

import kr.co.api.domain.port.out.ManageFilePort;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 파일 스토리지 어댑터 (Output Adapter)
 * ManageFilePort 구현체
 */
@Component
@Slf4j
public class FileStorageAdapter implements ManageFilePort {
    
    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;
    
    @Override
    public List<String> uploadFiles(String basePath, List<MultipartFile> files) {
        List<String> uploadedPaths = new ArrayList<>();
        
        for (MultipartFile file : files) {
            String uploadedPath = uploadSingleFile(basePath, file);
            uploadedPaths.add(uploadedPath);
        }
        
        return uploadedPaths;
    }
    
    @Override
    public String uploadSingleFile(String basePath, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
            }
            
            // 업로드 디렉토리 생성
            Path uploadDir = Paths.get(uploadBasePath, basePath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 고유한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = generateUniqueFilename() + fileExtension;
            
            // 파일 저장
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String relativePath = basePath + "/" + uniqueFilename;
            log.info("File uploaded successfully: {}", relativePath);
            
            return relativePath;
            
        } catch (IOException e) {
            log.error(\"Failed to upload file: {}\", file.getOriginalFilename(), e);
            throw new RuntimeException(\"File upload failed\", e);
        }
    }
    
    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadBasePath, filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted successfully: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            throw new RuntimeException("File deletion failed", e);
        }
    }
    
    @Override
    public byte[] downloadFile(String filePath) {
        try {
            Path path = Paths.get(uploadBasePath, filePath);
            if (!Files.exists(path)) {
                throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
            }
            
            byte[] fileData = Files.readAllBytes(path);
            log.info("File downloaded successfully: {}", filePath);
            
            return fileData;
            
        } catch (IOException e) {
            log.error("Failed to download file: {}", filePath, e);
            throw new RuntimeException("File download failed", e);
        }
    }
    
    @Override
    public boolean fileExists(String filePath) {
        Path path = Paths.get(uploadBasePath, filePath);
        return Files.exists(path);
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
    
    private String generateUniqueFilename() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid;
    }
}