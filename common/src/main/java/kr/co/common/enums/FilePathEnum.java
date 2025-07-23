package kr.co.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilePathEnum {

    MY_PET_IMAGE("/myPetImage", "내 반려동물 이미지 경로"),;

    private final String filePath;
    private final String description;
}
