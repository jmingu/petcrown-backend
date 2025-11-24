package kr.co.api.vote.domain;

import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 투표 파일 정보 도메인 모델
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteFileInfo {

    private final Long voteFileInfoId;
    private final VoteWeekly voteWeekly;
    private final String fileUrl;
    private final Long fileSize;
    private final String mimeType;
    private final String fileName;
    private final String originalFileName;

    /**
     * 투표 파일 정보 생성 (정적 팩토리 메서드)
     */
    public static VoteFileInfo createFileInfo(VoteWeekly voteWeekly, String fileUrl, Long fileSize,
                                             String mimeType, String fileName, String originalFileName) {
        // 필수값 검증
        if (voteWeekly == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        return new VoteFileInfo(
            null,               // voteFileInfoId (insert 시 null)
            voteWeekly,
            fileUrl,
            fileSize,
            mimeType,
            fileName,
            originalFileName
        );
    }
}
