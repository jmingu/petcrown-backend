package kr.co.api.common.service;

import kr.co.common.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final ObjectStorageService objectStorageService;

    /**
     * 이미지 파일들 업로드 (네이버 클라우드 Object Storage)
     */
    public List<String> uploadImageList(String subPath, List<MultipartFile> files) {
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String uploadedUrl = uploadImage(subPath, file);
                if (uploadedUrl != null) {
                    uploadedUrls.add(uploadedUrl);
                }
            }
        }

        return uploadedUrls;
    }

    /**
     * 단일 이미지 파일 업로드 (네이버 클라우드 Object Storage)
     */
    public String uploadImage(String subPath, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // ObjectStorageService를 사용하여 네이버 클라우드에 업로드
            String fileUrl = objectStorageService.uploadFile(file, subPath);
            log.info("File uploaded to cloud storage successfully: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("Failed to upload file to cloud storage: {}", file.getOriginalFilename(), e);
            return null;
        }
    }

    /**
     * 파일 삭제 (네이버 클라우드 Object Storage)
     */
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return false;
            }

            // ObjectStorageService를 사용하여 클라우드에서 삭제
            objectStorageService.deleteFile(fileUrl);
            log.info("File deleted from cloud storage successfully: {}", fileUrl);
            return true;

        } catch (Exception e) {
            log.error("Failed to delete file from cloud storage: {}", fileUrl, e);
            return false;
        }
    }
}