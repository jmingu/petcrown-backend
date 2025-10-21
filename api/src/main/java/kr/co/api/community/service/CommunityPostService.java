package kr.co.api.community.service;

import kr.co.api.common.mapper.FileInfoMapper;
import kr.co.api.common.service.FileService;
import kr.co.api.community.converter.domainEntity.CommunityPostDomainEntityConverter;
import kr.co.api.community.converter.dtoDomain.CommunityPostDtoDomainConverter;
import kr.co.api.community.converter.entityCommand.CommunityPostEntityCommandConverter;
import kr.co.api.community.domain.CommunityPost;
import kr.co.api.community.dto.command.CommunityPostInfoDto;
import kr.co.api.community.dto.command.CommunityPostRegistrationDto;
import kr.co.api.community.dto.command.CommunityPostUpdateDto;
import kr.co.api.community.mapper.CommunityPostMapper;
import kr.co.common.entity.community.CommunityPostEntity;
import kr.co.common.entity.community.CommunityPostQueryDto;
import kr.co.common.entity.file.FileInfoEntity;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
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

    private final CommunityPostDtoDomainConverter postDtoDomainConverter;
    private final CommunityPostDomainEntityConverter postDomainEntityConverter;
    private final CommunityPostEntityCommandConverter postEntityCommandConverter;
    private final CommunityPostMapper postMapper;
    private final FileInfoMapper fileInfoMapper;
    private final FileService fileService;

    private static final String COMMUNITY_REF_TABLE = "community";
    private static final String COMMUNITY_FILE_PATH = "community";

    @Transactional
    public void createPost(CommunityPostRegistrationDto postRegistrationDto) {
        CommunityPost post = postDtoDomainConverter.toPostForRegistration(postRegistrationDto);
        validatePostForRegistration(post);

        CommunityPostEntity postEntity = postDomainEntityConverter.toPostEntityForRegistration(post);
        postMapper.insertPost(postEntity);

        if (postRegistrationDto.getImageFiles() != null && !postRegistrationDto.getImageFiles().isEmpty()) {
            savePostFiles(postEntity.getPostId(), postRegistrationDto.getImageFiles(), postRegistrationDto.getCreateUserId());
        }

        log.info("Community post created successfully: postId={}", postEntity.getPostId());
    }

    @Transactional
    public CommunityPostInfoDto getPostDetail(Long postId) {
        CommunityPostQueryDto queryDto = postMapper.selectByPostId(postId);
        if (queryDto == null) {
            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        postMapper.incrementViewCount(postId);

        List<FileInfoEntity> fileInfoEntities = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postId);
        return postEntityCommandConverter.toPostInfoDto(queryDto, fileInfoEntities);
    }

    public CommunityPostInfoDto getPost(Long postId) {
        CommunityPostQueryDto queryDto = postMapper.selectByPostId(postId);
        if (queryDto == null) {
            throw new PetCrownException(BusinessCode.POST_NOT_FOUND);
        }

        List<FileInfoEntity> fileInfoEntities = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postId);
        return postEntityCommandConverter.toPostInfoDto(queryDto, fileInfoEntities);
    }

    public List<CommunityPostInfoDto> getAllPosts(int page, int size) {
        int offset = (page - 1) * size;
        List<CommunityPostQueryDto> queryDtos = postMapper.selectAllPosts(offset, size);

        List<CommunityPostInfoDto> postInfoDtos = new ArrayList<>();
        for (CommunityPostQueryDto queryDto : queryDtos) {
            List<FileInfoEntity> fileInfoEntities = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, queryDto.getPostId());
            postInfoDtos.add(postEntityCommandConverter.toPostInfoDto(queryDto, fileInfoEntities));
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

        CommunityPost post = postDtoDomainConverter.toPostForUpdate(postUpdateDto);
        validatePostForUpdate(post);

        postMapper.updatePost(postUpdateDto);

        if (postUpdateDto.getImageFiles() != null && !postUpdateDto.getImageFiles().isEmpty()) {
            // 기존 파일 조회
            List<FileInfoEntity> existingFiles = fileInfoMapper.selectByRefTableAndRefId(COMMUNITY_REF_TABLE, postUpdateDto.getPostId());

            // 새 파일 업로드 및 DB 저장
            savePostFiles(postUpdateDto.getPostId(), postUpdateDto.getImageFiles(), postUpdateDto.getUpdateUserId());

            // 기존 파일만 개별 삭제
            for (FileInfoEntity existingFile : existingFiles) {
                fileInfoMapper.deleteById(existingFile.getFileId(), postUpdateDto.getUpdateUserId());
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

        postMapper.deleteById(postId, deleteUserId);
        fileInfoMapper.deleteByRefTableAndRefId(COMMUNITY_REF_TABLE, postId, deleteUserId);

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
            postInfoDtos.add(postEntityCommandConverter.toPostInfoDto(queryDto, fileInfoEntities));
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
        List<FileInfoEntity> fileInfoEntities = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> imageUrls = fileService.uploadImageList(COMMUNITY_FILE_PATH, imageFiles);
            for (int i = 0; i < imageUrls.size(); i++) {
                fileInfoEntities.add(createFileInfoEntity(postId, "IMAGE", imageUrls.get(i), imageFiles.get(i), createUserId));
            }
        }

        if (!fileInfoEntities.isEmpty()) {
            fileInfoMapper.insertFileInfoBatch(fileInfoEntities);
        }
    }

    private FileInfoEntity createFileInfoEntity(Long postId, String fileType, String fileUrl,
                                                MultipartFile file, Long createUserId) {
        LocalDateTime now = LocalDateTime.now();
        String fileName = generateFileName(fileUrl);
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
    private String generateFileName(String fileUrl) {
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
