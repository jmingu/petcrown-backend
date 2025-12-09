package kr.co.api.event.service;

import kr.co.api.common.dto.FileInfoDto;
import kr.co.api.common.repository.FileInfoRepository;
import kr.co.api.common.service.FileService;
import kr.co.api.event.domain.model.Event;
import kr.co.api.event.dto.command.EventInfoDto;
import kr.co.api.event.dto.command.EventQueryDto;
import kr.co.api.event.dto.command.EventRegistrationDto;
import kr.co.api.event.dto.command.EventUpdateDto;
import kr.co.api.event.repository.EventRepository;
import kr.co.common.enums.BusinessCode;
import kr.co.common.exception.PetCrownException;
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
public class EventService {

    private final EventRepository eventRepository;
    private final FileInfoRepository fileInfoRepository;
    private final FileService fileService;

    private static final String EVENT_REF_TABLE = "event";
    private static final String EVENT_FILE_PATH = "event";

    /**
     * 이벤트 등록
     */
    @Transactional
    public void createEvent(EventRegistrationDto eventRegistrationDto) {

        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        Event event = Event.createEvent(
                eventRegistrationDto.getTitle(),
                eventRegistrationDto.getContent(),
                eventRegistrationDto.getContentType(),
                eventRegistrationDto.getStartDate(),
                eventRegistrationDto.getEndDate(),
                eventRegistrationDto.getCreateUserId()
        );

        // 비즈니스 규칙 검증
        validateEventForRegistration(event);

        // Repository에 도메인 객체 직접 전달
        Long eventId = eventRepository.insertEvent(event);

        // 파일 업로드 처리
        if (eventRegistrationDto.getThumbnailFile() != null ||
            (eventRegistrationDto.getImageFiles() != null && !eventRegistrationDto.getImageFiles().isEmpty())) {
            saveEventFiles(eventId, eventRegistrationDto.getThumbnailFile(),
                          eventRegistrationDto.getImageFiles(), eventRegistrationDto.getCreateUserId());
        }

        log.info("Event created successfully: eventId={}", eventId);
    }

    /**
     * 이벤트 상세 조회 (조회수 증가)
     */
    @Transactional
    public EventInfoDto getEventDetail(Long eventId) {
        EventQueryDto eventQueryDto = eventRepository.selectByEventId(eventId);
        if (eventQueryDto == null) {
            throw new PetCrownException(BusinessCode.EVENT_NOT_FOUND);
        }

        // 조회수 증가
        eventRepository.incrementViewCount(eventId);

        // 파일 정보 조회
        List<FileInfoDto> fileInfoDtos = fileInfoRepository.selectByRefTableAndRefId(EVENT_REF_TABLE, eventId);

        // Dto를 CommandDto로 변환 (생성자 직접 호출)
        return convertToEventInfoDto(eventQueryDto, fileInfoDtos);
    }

    /**
     * 이벤트 조회 (조회수 증가 없음)
     */
    public EventInfoDto getEvent(Long eventId) {
        EventQueryDto eventQueryDto = eventRepository.selectByEventId(eventId);
        if (eventQueryDto == null) {
            throw new PetCrownException(BusinessCode.EVENT_NOT_FOUND);
        }

        // 파일 정보 조회
        List<FileInfoDto> fileInfoDtos = fileInfoRepository.selectByRefTableAndRefId(EVENT_REF_TABLE, eventId);

        return convertToEventInfoDto(eventQueryDto, fileInfoDtos);
    }

    /**
     * 활성화된 이벤트 목록 조회
     */
    public List<EventInfoDto> getActiveEvents(int page, int size) {
        int offset = (page - 1) * size;
        List<EventQueryDto> eventQueryDtos = eventRepository.selectActiveEvents(offset, size);

        // 각 이벤트의 썸네일만 조회
        List<EventInfoDto> eventInfoDtos = new ArrayList<>();
        for (EventQueryDto eventQueryDto : eventQueryDtos) {
            List<FileInfoDto> fileInfoDtos = fileInfoRepository.selectByRefTableAndRefId(EVENT_REF_TABLE, eventQueryDto.getEventId());
            eventInfoDtos.add(convertToEventInfoDto(eventQueryDto, fileInfoDtos));
        }

        return eventInfoDtos;
    }

    /**
     * 전체 이벤트 목록 조회 (관리자용)
     */
    public List<EventInfoDto> getAllEvents(int page, int size) {
        int offset = (page - 1) * size;
        List<EventQueryDto> eventQueryDtos = eventRepository.selectAllEvents(offset, size);

        // 각 이벤트의 썸네일만 조회
        List<EventInfoDto> eventInfoDtos = new ArrayList<>();
        for (EventQueryDto eventQueryDto : eventQueryDtos) {
            List<FileInfoDto> fileInfoDtos = fileInfoRepository.selectByRefTableAndRefId(EVENT_REF_TABLE, eventQueryDto.getEventId());
            eventInfoDtos.add(convertToEventInfoDto(eventQueryDto, fileInfoDtos));
        }

        return eventInfoDtos;
    }

    /**
     * 활성화된 이벤트 개수 조회
     */
    public int getActiveEventsCount() {
        return eventRepository.countActiveEvents();
    }

    /**
     * 전체 이벤트 개수 조회
     */
    public int getAllEventsCount() {
        return eventRepository.countAllEvents();
    }

    /**
     * 이벤트 수정
     */
    @Transactional
    public void updateEvent(EventUpdateDto eventUpdateDto) {

        // 기존 이벤트 조회
        EventQueryDto existingEvent = eventRepository.selectByEventId(eventUpdateDto.getEventId());
        if (existingEvent == null) {
            throw new PetCrownException(BusinessCode.EVENT_NOT_FOUND);
        }

        // CommandDto → Domain 변환 (정적 팩토리 메서드 사용)
        Event event = Event.createEvent(
                eventUpdateDto.getTitle(),
                eventUpdateDto.getContent(),
                eventUpdateDto.getContentType(),
                eventUpdateDto.getStartDate(),
                eventUpdateDto.getEndDate(),
                eventUpdateDto.getUpdateUserId()
        );

        // 비즈니스 규칙 검증
        validateEventForUpdate(event);

        // 이벤트 정보 업데이트
        eventRepository.updateEvent(eventUpdateDto);

        // 파일 업데이트 처리 (기존 파일 조회 → 새 파일 업로드 → DB 저장 → 기존 파일 개별 삭제)
        if (eventUpdateDto.getThumbnailFile() != null) {
            // 기존 썸네일 조회
            List<FileInfoDto> existingThumbnails = fileInfoRepository.selectByRefTableAndRefId(EVENT_REF_TABLE, eventUpdateDto.getEventId())
                .stream().filter(f -> "THUMBNAIL".equals(f.getFileType())).toList();

            // 새 썸네일 업로드 및 DB 저장
            String thumbnailUrl = fileService.uploadImage(EVENT_FILE_PATH, eventUpdateDto.getThumbnailFile());
            if (thumbnailUrl != null) {
                FileInfoDto thumbnailDto = createFileInfoDto(eventUpdateDto.getEventId(), "THUMBNAIL",
                                                             thumbnailUrl, eventUpdateDto.getThumbnailFile(),
                                                             eventUpdateDto.getUpdateUserId());
                fileInfoRepository.insertFileInfo(thumbnailDto); // fileId 반환되지만 사용하지 않음

                // 기존 썸네일만 개별 삭제
                for (FileInfoDto existing : existingThumbnails) {
                    fileInfoRepository.deleteById(existing.getFileId(), eventUpdateDto.getUpdateUserId());
                }
            }
        }

        if (eventUpdateDto.getImageFiles() != null && !eventUpdateDto.getImageFiles().isEmpty()) {
            // 기존 이미지 조회
            List<FileInfoDto> existingImages = fileInfoRepository.selectByRefTableAndRefId(EVENT_REF_TABLE, eventUpdateDto.getEventId())
                .stream().filter(f -> "IMAGE".equals(f.getFileType())).toList();

            // 새 이미지 업로드 및 DB 저장
            List<String> imageUrls = fileService.uploadImageList(EVENT_FILE_PATH, eventUpdateDto.getImageFiles());
            if (!imageUrls.isEmpty()) {
                List<FileInfoDto> imageDtos = new ArrayList<>();
                for (int i = 0; i < imageUrls.size(); i++) {
                    imageDtos.add(createFileInfoDto(eventUpdateDto.getEventId(), "IMAGE",
                                                    imageUrls.get(i), eventUpdateDto.getImageFiles().get(i),
                                                    eventUpdateDto.getUpdateUserId()));
                }
                fileInfoRepository.insertFileInfoBatch(imageDtos);

                // 기존 이미지만 개별 삭제
                for (FileInfoDto existing : existingImages) {
                    fileInfoRepository.deleteById(existing.getFileId(), eventUpdateDto.getUpdateUserId());
                }
            }
        }

        log.info("Event updated successfully: eventId={}", eventUpdateDto.getEventId());
    }

    /**
     * 이벤트 삭제 (논리 삭제)
     */
    @Transactional
    public void deleteEvent(Long eventId, Long deleteUserId) {
        EventQueryDto existingEvent = eventRepository.selectByEventId(eventId);
        if (existingEvent == null) {
            throw new PetCrownException(BusinessCode.EVENT_NOT_FOUND);
        }

        eventRepository.deleteById(eventId, deleteUserId);
        fileInfoRepository.deleteByRefTableAndRefId(EVENT_REF_TABLE, eventId, deleteUserId);

        log.info("Event deleted successfully: eventId={}", eventId);
    }

    /**
     * 제목으로 이벤트 검색
     */
    public List<EventInfoDto> searchEventsByTitle(String title, int page, int size) {
        int offset = (page - 1) * size;
        List<EventQueryDto> eventQueryDtos = eventRepository.searchByTitle(title, offset, size);

        List<EventInfoDto> eventInfoDtos = new ArrayList<>();
        for (EventQueryDto eventQueryDto : eventQueryDtos) {
            List<FileInfoDto> fileInfoDtos = fileInfoRepository.selectByRefTableAndRefId(EVENT_REF_TABLE, eventQueryDto.getEventId());
            eventInfoDtos.add(convertToEventInfoDto(eventQueryDto, fileInfoDtos));
        }

        return eventInfoDtos;
    }

    /**
     * 제목으로 검색된 이벤트 개수
     */
    public int getSearchEventsCountByTitle(String title) {
        return eventRepository.countSearchByTitle(title);
    }

    // ========================
    // 비즈니스 로직 메서드들
    // ========================

    /**
     * 이벤트 등록 검증
     */
    private void validateEventForRegistration(Event event) {
        if (event == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 시작일과 종료일 검증
        if (event.getEndDate() != null && event.getStartDate().isAfter(event.getEndDate())) {
            throw new PetCrownException(BusinessCode.INVALID_DATE_RANGE);
        }
    }

    /**
     * 이벤트 수정 검증
     */
    private void validateEventForUpdate(Event event) {
        if (event == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }

        // 시작일과 종료일 검증
        if (event.getEndDate() != null && event.getStartDate().isAfter(event.getEndDate())) {
            throw new PetCrownException(BusinessCode.INVALID_DATE_RANGE);
        }
    }

    /**
     * 이벤트 파일 저장
     */
    private void saveEventFiles(Long eventId, MultipartFile thumbnailFile, List<MultipartFile> imageFiles, Long createUserId) {
        List<FileInfoDto> fileInfoDtos = new ArrayList<>();

        // 썸네일 업로드
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String thumbnailUrl = fileService.uploadImage(EVENT_FILE_PATH, thumbnailFile);
            if (thumbnailUrl != null) {
                fileInfoDtos.add(createFileInfoDto(eventId, "THUMBNAIL", thumbnailUrl, thumbnailFile, createUserId));
            }
        }

        // 이미지 파일들 업로드
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> imageUrls = fileService.uploadImageList(EVENT_FILE_PATH, imageFiles);
            for (int i = 0; i < imageUrls.size(); i++) {
                fileInfoDtos.add(createFileInfoDto(eventId, "IMAGE", imageUrls.get(i), imageFiles.get(i), createUserId));
            }
        }

        // 배치 저장
        if (!fileInfoDtos.isEmpty()) {
            fileInfoRepository.insertFileInfoBatch(fileInfoDtos);
        }
    }

    /**
     * FileInfoDto 생성
     */
    private FileInfoDto createFileInfoDto(Long eventId, String fileType, String fileUrl,
                                          MultipartFile file, Long createUserId) {
        String fileName = generateFileName(fileUrl);
        String originalFileName = file.getOriginalFilename();

        return new FileInfoDto(
                EVENT_REF_TABLE,            // refTable
                eventId,                    // refId
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

    /**
     * EventQueryDto → EventInfoDto 변환 (생성자 직접 호출)
     */
    private EventInfoDto convertToEventInfoDto(EventQueryDto eventQueryDto, List<FileInfoDto> fileInfoDtos) {
        String thumbnailUrl = null;
        List<String> imageUrls = new ArrayList<>();

        for (FileInfoDto fileInfoDto : fileInfoDtos) {
            if ("THUMBNAIL".equals(fileInfoDto.getFileType())) {
                thumbnailUrl = fileInfoDto.getFileUrl();
            } else if ("IMAGE".equals(fileInfoDto.getFileType())) {
                imageUrls.add(fileInfoDto.getFileUrl());
            }
        }

        return new EventInfoDto(
                eventQueryDto.getEventId(),
                eventQueryDto.getTitle(),
                eventQueryDto.getContent(),
                eventQueryDto.getContentType(),
                eventQueryDto.getStartDate(),
                eventQueryDto.getEndDate(),
                eventQueryDto.getViewCount(),
                eventQueryDto.getCreateDate(),
                eventQueryDto.getCreateUserId(),
                thumbnailUrl,
                imageUrls
        );
    }
}
