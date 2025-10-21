package kr.co.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectStorageService {

    private final AmazonS3 amazonS3;

    @Value("${naver.cloud.object-storage.bucket}")
    private String bucketName;

    @Value("${naver.cloud.object-storage.base-url}")
    private String baseUrl;

    /**
     * 단일 파일 업로드
     *
     * @param multipartFile 업로드할 파일
     * @param folderPath    저장할 폴더 경로 (예: "pets/images", "users/profiles")
     * @return 업로드된 파일의 공개 URL
     */
    public String uploadFile(MultipartFile multipartFile, String folderPath) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        try {
            // 고유한 파일명 생성: yyyyMMdd_uuid.확장자
            String originalFilename = multipartFile.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String randomId = UUID.randomUUID().toString().substring(0, 8); // 8자리만 사용
            String uniqueFileName = String.format("%s_%s%s", today, randomId, fileExtension);

            // 전체 경로 생성 (폴더경로/파일명)
            String key = folderPath + "/" + uniqueFileName;

            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    multipartFile.getInputStream(),
                    metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead); // 공개 읽기 권한

            amazonS3.putObject(putObjectRequest);

            // 공개 URL 반환
            String fileUrl = baseUrl + "/" + key;
            
            log.info("파일 업로드 성공: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 다중 파일 업로드
     *
     * @param multipartFiles 업로드할 파일 리스트
     * @param folderPath     저장할 폴더 경로
     * @return 업로드된 파일들의 공개 URL 리스트
     */
    public List<String> uploadFiles(List<MultipartFile> multipartFiles, String folderPath) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            if (file != null && !file.isEmpty()) {
                String uploadedUrl = uploadFile(file, folderPath);
                uploadedUrls.add(uploadedUrl);
            }
        }

        return uploadedUrls;
    }

    /**
     * 파일 삭제
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    public void deleteFile(String fileUrl) {
        try {
            // URL에서 키 추출
            String key = extractKeyFromUrl(fileUrl);
            
            amazonS3.deleteObject(bucketName, key);
            log.info("파일 삭제 성공: {}", fileUrl);
            
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * URL로 파일 삭제 (별칭 메서드)
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    public void deleteFileFromUrl(String fileUrl) {
        deleteFile(fileUrl);
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 확장자를 제외한 파일명 추출
     */
    private String getFileNameWithoutExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return filename;
        }
        return filename.substring(0, filename.lastIndexOf("."));
    }

    /**
     * URL에서 Object Storage 키 추출
     */
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
            throw new IllegalArgumentException("잘못된 파일 URL입니다.");
        }
        return fileUrl.substring(baseUrl.length() + 1);
    }
}