package kr.co.api.community.service;

import kr.co.api.common.mapper.FileInfoMapper;
import kr.co.api.community.domain.CommunityPost;
import kr.co.api.community.dto.command.CommunityPostInfoDtailDto;
import kr.co.api.community.dto.command.CommunityPostInfoDto;
import kr.co.api.community.dto.command.CommunityPostRegistrationDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import kr.co.api.community.mapper.CommunityPostMapper;
import kr.co.common.entity.community.CommunityPostEntity;
import kr.co.common.entity.community.CommunityPostQueryDto;
import kr.co.common.entity.file.FileInfoEntity;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
import kr.co.common.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommunityPostService {

    private final CommunityPostMapper postMapper;
    private final FileInfoMapper fileInfoMapper;
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

        // Domain → Entity 변환 (생성자 직접 호출)
        CommunityPostEntity postEntity = new CommunityPostEntity(
                post.getPostId(),
                post.getUserId(),
                post.getCategory(),
                post.getTitle() != null ? post.getTitle().getValue() : null,
                post.getContent() != null ? post.getContent().getValue() : null,
                post.getContentType() != null ? post.getContentType().getValue() : null,
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getIsPinned(),
                post.getPinOrder(),
                LocalDateTime.now(),  // createDate
                post.getCreateUserId(),
                LocalDateTime.now(),  // updateDate
                post.getCreateUserId(),
                null,  // deleteDate
                null   // deleteUserId
        );
        postMapper.insertPost(postEntity);

        if (postRegistrationDto.getImageFiles() != null && !postRegistrationDto.getImageFiles().isEmpty()) {
            savePostFiles(postEntity.getPostId(), postRegistrationDto.getImageFiles(), postRegistrationDto.getCreateUserId());
        }

        log.info("Community post created successfully: postId={}", postEntity.getPostId());
    }

    @Transactional
    public CommunityPostInfoDtailDto getPostDetail(Long postId, Long userId) {
        CommunityPostQueryDto queryDto = postMapper.selectByPostId(postId);
        if (queryDto == null) {

            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        postMapper.incrementViewCount(postId);

        List<FileInfoEntity> fileInfoEntities = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postId);

        // Entity → CommandDto 변환 (생성자 직접 호출)
        List<String> imageUrls = new ArrayList<>();
        for (FileInfoEntity fileInfoEntity : fileInfoEntities) {
            imageUrls.add(fileInfoEntity.getFileUrl());
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
        List<CommunityPostQueryDto> queryDtos = postMapper.selectAllPosts(offset, size);

        List<CommunityPostInfoDto> postInfoDtos = new ArrayList<>();
        for (CommunityPostQueryDto queryDto : queryDtos) {
            List<FileInfoEntity> fileInfoEntities = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, queryDto.getPostId());

            // Entity → CommandDto 변환 (생성자 직접 호출)
            List<String> imageUrls = new ArrayList<>();
            for (FileInfoEntity fileInfoEntity : fileInfoEntities) {
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
        return postMapper.countAllPosts();
    }

    @Transactional
    public void updatePost(CommunityPostUpdateDto postUpdateDto) {
        CommunityPostQueryDto existingPost = postMapper.selectByPostId(postUpdateDto.getPostId());
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

        postMapper.updatePost(postUpdateDto);

        if (postUpdateDto.getImageFiles() != null && !postUpdateDto.getImageFiles().isEmpty()) {
            // 1. 기존 파일 조회
            List<FileInfoEntity> existingFiles = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postUpdateDto.getPostId());

            // 2. 새 파일 업로드 및 DB 저장
            savePostFiles(postUpdateDto.getPostId(), postUpdateDto.getImageFiles(), postUpdateDto.getUpdateUserId());

            // 3. 기존 파일 삭제 (DB + Object Storage)
            if (!existingFiles.isEmpty()) {
                for (FileInfoEntity existingFile : existingFiles) {
                    // DB에서 삭제
                    fileInfoMapper.deleteById(existingFile.getFileId(), postUpdateDto.getUpdateUserId());

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
        }

        log.info("Community post updated successfully: postId={}", postUpdateDto.getPostId());
    }

    @Transactional
    public void deletePost(Long postId, Long deleteUserId) {
        CommunityPostQueryDto existingPost = postMapper.selectByPostId(postId);
        if (existingPost == null) {
            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        // 1. 기존 파일 조회 및 삭제 (Object Storage)
        List<FileInfoEntity> existingFiles = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postId);
        if (!existingFiles.isEmpty()) {
            for (FileInfoEntity existingFile : existingFiles) {
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
        fileInfoMapper.deleteByRefTableAndRefId(COMMUNITY_REF_TABLE, postId, deleteUserId);

        // 3. 게시글 삭제
        postMapper.deleteById(postId, deleteUserId);

        log.info("Community post deleted successfully: postId={}", postId);
    }

    @Transactional
    public void likePost(Long postId) {
        CommunityPostQueryDto existingPost = postMapper.selectByPostId(postId);
        if (existingPost == null) {
            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        postMapper.incrementLikeCount(postId);
        log.info("Community post liked: postId={}", postId);
    }

    public List<CommunityPostInfoDto> searchPostsByTitle(String title, int page, int size) {
        int offset = (page - 1) * size;
        List<CommunityPostQueryDto> queryDtos = postMapper.searchByTitle(title, offset, size);

        List<CommunityPostInfoDto> postInfoDtos = new ArrayList<>();
        for (CommunityPostQueryDto queryDto : queryDtos) {
            List<FileInfoEntity> fileInfoEntities = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, queryDto.getPostId());

            // Entity → CommandDto 변환 (생성자 직접 호출)
            List<String> imageUrls = new ArrayList<>();
            for (FileInfoEntity fileInfoEntity : fileInfoEntities) {
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

        List<FileInfoEntity> fileInfoEntities = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile != null && !imageFile.isEmpty()) {
                // ObjectStorageService를 사용하여 네이버 클라우드에 업로드
                String imageUrl = objectStorageService.uploadFile(imageFile, COMMUNITY_FOLDER_PATH);

                // FileInfoEntity 생성
                FileInfoEntity fileInfoEntity = createFileInfoEntity(
                    postId, "IMAGE", imageUrl, imageFile, createUserId
                );
                fileInfoEntities.add(fileInfoEntity);

                log.info("Community post image uploaded: postId={}, imageUrl={}", postId, imageUrl);
            }
        }

        if (!fileInfoEntities.isEmpty()) {
            fileInfoMapper.insertFileInfoBatch(fileInfoEntities);
        }
    }

    private FileInfoEntity createFileInfoEntity(Long postId, String fileType, String fileUrl,
                                                MultipartFile file, Long createUserId) {
        LocalDateTime now = LocalDateTime.now();
        String fileName = extractFileNameFromUrl(fileUrl);
        String originalFileName = file.getOriginalFilename();

        return new FileInfoEntity(
                null,                       // fileId
                now,                        // createDate
                createUserId,               // createUserId
                now,                        // updatedDate
                createUserId,               // updateUserId
                null,                       // deleteDate
                null,                       // deleteUserId
                COMMUNITY_REF_TABLE,        // refTable
                postId,                     // refId
                fileType,                   // fileType
                null,                       // sortOrder
                fileUrl,                    // fileUrl
                file.getSize(),             // fileSize
                file.getContentType(),      // mimeType
                fileName,                   // fileName
                originalFileName            // originalFileName
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
