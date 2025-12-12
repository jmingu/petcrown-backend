package kr.co.api.community.service;

import kr.co.api.common.repository.FileInfoRepository;
import kr.co.api.community.domain.model.CommunityPost;
import kr.co.api.community.dto.command.CommunityPostInfoDtailDto;
import kr.co.api.community.dto.command.CommunityPostInfoDto;
import kr.co.api.community.dto.command.CommunityPostRegistrationDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import kr.co.api.common.dto.FileInfoDto;
import kr.co.api.community.dto.command.CommunityPostQueryDto;
import kr.co.api.community.repository.CommunityPostRepository;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ObjectStorageService objectStorageService;

    private static final String COMMUNITY_REF_TABLE = "community";
    private static final String COMMUNITY_FOLDER_PATH = "community";

    @Transactional
    public void createPost(CommunityPostRegistrationDto postRegistrationDto) {
        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        CommunityPost post = CommunityPost.createPost(
                postRegistrationDto.getUserId(),
                postRegistrationDto.getCategory(),
                postRegistrationDto.getTitle(),
                postRegistrationDto.getContent(),
                postRegistrationDto.getContentType(),
                postRegistrationDto.getCreateUserId()
        );
        validatePostForRegistration(post);

        // Repository에 도메인 객체 직접 전달
        Long postId = postRepository.insertPost(post);

        if (postRegistrationDto.getImageFiles() != null && !postRegistrationDto.getImageFiles().isEmpty()) {
            savePostFiles(postId, postRegistrationDto.getImageFiles(), postRegistrationDto.getCreateUserId());
        }

        log.info("Community post created successfully: postId={}", postId);
    }

    @Transactional
    public CommunityPostInfoDtailDto getPostDetail(Long postId, Long userId) {
        CommunityPostQueryDto queryDto = postRepository.selectByPostId(postId);
        if (queryDto == null) {

            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        postRepository.incrementViewCount(postId);

        List<FileInfoDto> fileInfoDtos = fileInfoRepository.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postId);

        // Dto → CommandDto 변환 (생성자 직접 호출)
        List<String> imageUrls = new ArrayList<>();
        for (FileInfoDto fileInfoDto : fileInfoDtos) {
            imageUrls.add(fileInfoDto.getFileUrl());
        }

        return new CommunityPostInfoDtailDto(
                queryDto.getPostId(),
                queryDto.getNickname(),
                queryDto.getCategory(),
                queryDto.getTitle(),
                queryDto.getContent(),
                queryDto.getContentType(),
                queryDto.getViewCount(),
                queryDto.getLikeCount(),
                queryDto.getCommentCount(),
                queryDto.getIsPinned(),
                queryDto.getPinOrder(),
                queryDto.getCreateDate(),
                imageUrls,
                queryDto.getUserId().equals(userId) ? "Y" : "N"
        );
    }



    public List<CommunityPostInfoDto> getAllPosts(int page, int size) {
        int offset = (page - 1) * size;
        List<CommunityPostQueryDto> queryDtos = postRepository.selectAllPosts(offset, size);

        List<CommunityPostInfoDto> postInfoDtos = new ArrayList<>();
        for (CommunityPostQueryDto queryDto : queryDtos) {
            List<FileInfoDto> fileInfoEntities = fileInfoRepository.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, queryDto.getPostId());

            // Entity → CommandDto 변환 (생성자 직접 호출)
            List<String> imageUrls = new ArrayList<>();
            for (FileInfoDto fileInfoEntity : fileInfoEntities) {
                imageUrls.add(fileInfoEntity.getFileUrl());
            }

            postInfoDtos.add(new CommunityPostInfoDto(
                    queryDto.getPostId(),
                    queryDto.getNickname(),
                    queryDto.getCategory(),
                    queryDto.getTitle(),
                    queryDto.getContent(),
                    queryDto.getContentType(),
                    queryDto.getViewCount(),
                    queryDto.getLikeCount(),
                    queryDto.getCommentCount(),
                    queryDto.getIsPinned(),
                    queryDto.getPinOrder(),
                    queryDto.getCreateDate(),
                    imageUrls
            ));
        }

        return postInfoDtos;
    }

    public int getAllPostsCount() {
        return postRepository.countAllPosts();
    }

    @Transactional
    public void updatePost(CommunityPostUpdateDto postUpdateDto) {
        CommunityPostQueryDto existingPost = postRepository.selectByPostId(postUpdateDto.getPostId());
        if (existingPost == null) {
            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        CommunityPost post = CommunityPost.createPost(
                existingPost.getUserId(),
                postUpdateDto.getCategory(),
                postUpdateDto.getTitle(),
                postUpdateDto.getContent(),
                postUpdateDto.getContentType(),
                postUpdateDto.getUpdateUserId()
        );
        validatePostForUpdate(post);

        postRepository.updatePost(postUpdateDto);

        if (postUpdateDto.getImageFiles() != null && !postUpdateDto.getImageFiles().isEmpty()) {
            // 새 이미지 파일이 있는 경우: 기존 이미지 삭제 + 새 이미지 업로드
            // 1. 기존 파일 조회
            List<FileInfoDto> existingFiles = fileInfoRepository.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postUpdateDto.getPostId());

            // 2. 새 파일 업로드 및 DB 저장
            savePostFiles(postUpdateDto.getPostId(), postUpdateDto.getImageFiles(), postUpdateDto.getUpdateUserId());

            // 3. 기존 파일 삭제 (DB + Object Storage)
            deleteExistingFiles(existingFiles, postUpdateDto.getUpdateUserId());

        } else if (postUpdateDto.getImageUrls() != null && postUpdateDto.getImageUrls().isEmpty()) {
            // 이미지를 없애는 수정일 시 (imageUrls가 빈 배열인 경우)
            List<FileInfoDto> existingFiles = fileInfoRepository.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postUpdateDto.getPostId());
            deleteExistingFiles(existingFiles, postUpdateDto.getUpdateUserId());
        }

        log.info("Community post updated successfully: postId={}", postUpdateDto.getPostId());
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, Long deleteUserId) {
        CommunityPostQueryDto existingPost = postRepository.selectByPostId(postId);
        if (existingPost == null) {
            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        // 1. 기존 파일 조회 및 삭제 (Object Storage)
        List<FileInfoDto> existingFiles = fileInfoRepository.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postId);
        if (!existingFiles.isEmpty()) {
            for (FileInfoDto existingFile : existingFiles) {
                if (existingFile.getFileUrl() != null) {
                    try {
                        objectStorageService.deleteFileFromUrl(existingFile.getFileUrl());
                        log.info("Community post image deleted: {}", existingFile.getFileUrl());
                    } catch (Exception e) {
                        log.warn("Failed to delete community post image (continuing): {}", e.getMessage());
                    }
                }
            }
        }

        // 2. DB에서 파일 정보 삭제
        fileInfoRepository.deleteByRefTableAndRefId(COMMUNITY_REF_TABLE, postId, deleteUserId);

        // 3. 게시글 삭제
        postRepository.deleteById(postId, deleteUserId);

        log.info("Community post deleted successfully: postId={}", postId);
    }

    @Transactional
    public void likePost(Long postId) {
        CommunityPostQueryDto existingPost = postRepository.selectByPostId(postId);
        if (existingPost == null) {
            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        postRepository.incrementLikeCount(postId);
        log.info("Community post liked: postId={}", postId);
    }

    public List<CommunityPostInfoDto> searchPostsByTitle(String title, int page, int size) {
        int offset = (page - 1) * size;
        List<CommunityPostQueryDto> queryDtos = postRepository.searchByTitle(title, offset, size);

        List<CommunityPostInfoDto> postInfoDtos = new ArrayList<>();
        for (CommunityPostQueryDto queryDto : queryDtos) {
            List<FileInfoDto> fileInfoEntities = fileInfoRepository.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, queryDto.getPostId());

            // Entity → CommandDto 변환 (생성자 직접 호출)
            List<String> imageUrls = new ArrayList<>();
            for (FileInfoDto fileInfoEntity : fileInfoEntities) {
                imageUrls.add(fileInfoEntity.getFileUrl());
            }

            postInfoDtos.add(new CommunityPostInfoDto(
                    queryDto.getPostId(),
                    queryDto.getNickname(),
                    queryDto.getCategory(),
                    queryDto.getTitle(),
                    queryDto.getContent(),
                    queryDto.getContentType(),
                    queryDto.getViewCount(),
                    queryDto.getLikeCount(),
                    queryDto.getCommentCount(),
                    queryDto.getIsPinned(),
                    queryDto.getPinOrder(),
                    queryDto.getCreateDate(),
                    imageUrls
            ));
        }

        return postInfoDtos;
    }

    private void validatePostForRegistration(CommunityPost post) {
        if (post == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
    }

    private void validatePostForUpdate(CommunityPost post) {
        if (post == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
    }

    private void savePostFiles(Long postId, List<MultipartFile> imageFiles, Long createUserId) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return;
        }

        List<FileInfoDto> fileInfoList = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile != null && !imageFile.isEmpty()) {
                // ObjectStorageService를 사용하여 네이버 클라우드에 업로드
                String imageUrl = objectStorageService.uploadFile(imageFile, COMMUNITY_FOLDER_PATH);

                // FileInfoDto 생성
                FileInfoDto fileInfoDto = createFileInfoDto(
                    postId, "IMAGE", imageUrl, imageFile, createUserId
                );
                fileInfoList.add(fileInfoDto);

                log.info("Community post image uploaded: postId={}, imageUrl={}", postId, imageUrl);
            }
        }

        if (!fileInfoList.isEmpty()) {
            fileInfoRepository.insertFileInfoBatch(fileInfoList);
        }
    }

    private void deleteExistingFiles(List<FileInfoDto> existingFiles, Long deleteUserId) {
        if (existingFiles == null || existingFiles.isEmpty()) {
            return;
        }

        for (FileInfoDto existingFile : existingFiles) {
            // DB에서 삭제
            fileInfoRepository.deleteById(existingFile.getFileId(), deleteUserId);

            // Object Storage에서 삭제
            if (existingFile.getFileUrl() != null) {
                try {
                    objectStorageService.deleteFileFromUrl(existingFile.getFileUrl());
                    log.info("Community post image deleted: {}", existingFile.getFileUrl());
                } catch (Exception e) {
                    log.warn("Failed to delete community post image (continuing): {}", e.getMessage());
                }
            }
        }
    }

    private FileInfoDto createFileInfoDto(Long postId, String fileType, String fileUrl,
                                          MultipartFile file, Long createUserId) {
        String fileName = extractFileNameFromUrl(fileUrl);
        String originalFileName = file.getOriginalFilename();

        return new FileInfoDto(
                COMMUNITY_REF_TABLE,        // refTable
                postId,                     // refId
                fileType,                   // fileType
                null,                       // sortOrder
                fileUrl,                    // fileUrl
                file.getSize(),             // fileSize
                file.getContentType(),      // mimeType
                fileName,                   // fileName
                originalFileName,           // originalFileName
                createUserId                // createUserId
        );
    }

    /**
     * URL에서 파일명 추출
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        // URL에서 마지막 '/' 이후의 파일명 추출
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < fileUrl.length() - 1) {
            return fileUrl.substring(lastSlashIndex + 1);
        }
        return fileUrl;
    }
}
