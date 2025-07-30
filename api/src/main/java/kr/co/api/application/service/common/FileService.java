package kr.co.api.application.service.common;

import kr.co.api.application.port.in.common.FileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements FileUseCase {

    @Value("${file.path.my-pet-image}")
    private String baseFilePath;
    private final Environment environment;

    /**
     * 이미지 업로드
     * @param filePath 업로드할 파일 경로
     * @param imageList 업로드할 이미지 리스트
     * @return 업로드된 이미지의 경로 리스트
     */
    @Override
    public List<String> uploadImageList(String filePath, List<MultipartFile> imageList) {

        String os = System.getProperty("os.name").toLowerCase();
        String[] activeProfiles = environment.getActiveProfiles();

        boolean isLocalEnv = false;

        // local을 개발환경일땐 baseFilePath를 시스템에 맞게 변경
        for (String profile : activeProfiles) {
            if (profile.equalsIgnoreCase("local")) {
                isLocalEnv = true;
                break;
            }
        }
        if(isLocalEnv) {
            if (os.contains("mac")) { // macOS 환경
                baseFilePath = "/Users";
            }
        }


        String fullPath = (baseFilePath + filePath)
                .replace("/", File.separator)
                .replace("\\", File.separator);
        File directory = new File(fullPath);

        log.info("baseFilePath: {}", baseFilePath);
        log.info("filePath: {}", filePath);
        log.info("fullPath: {}", fullPath);


        // 디렉토리가 없으면 생성
        log.info("디렉토리 생성 시도: {}", fullPath);

        if (!directory.exists()) {
            try {
                directory.mkdir(); // 디렉터리 생성.
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        List<String> savedFilePaths = new ArrayList<>();

        for (MultipartFile image : imageList) {
            try {
                // 현재 날짜와 시간으로 파일 이름 생성
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String originalFilename = image.getOriginalFilename();
                String uuid = UUID.randomUUID().toString().substring(0, 8);
                String newFilename = timestamp + "_" + uuid + "_" + originalFilename;

                // 파일 저장
                File destinationFile = new File(fullPath, newFilename);
                image.transferTo(destinationFile);

                // 저장된 파일 경로 추가
                savedFilePaths.add(destinationFile.getAbsolutePath());

                log.info("파일 저장 성공: {}", destinationFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("파일 저장 실패: {}", e.getMessage());
                throw new RuntimeException("파일 저장 실패", e);
            }
        }

        return savedFilePaths;

    }
}
