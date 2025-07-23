package kr.co.api.application.port.in.common;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUseCase {

    /**
     * 이미지 업로드
     */
    List<String> uploadImageList(String filePath, List<MultipartFile> imageList);
}
