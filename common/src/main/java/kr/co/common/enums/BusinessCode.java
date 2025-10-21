package kr.co.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessCode {

    // 공통(3000~)
    INVALID_REQUEST(3000, "요청 값이 잘못되었습니다", "Invalid request value"),
    MISSING_REQUIRED_VALUE(3001, "값이 없습니다", "Missing required value"), // null값
    EMPTY_VALUE(3002, "빈값이 있습니다.", "Empty value"), // "" 같은 공백값
    ALREADY_PROCESSED(3003, "이미 처리되었습니다", "Already processed"),
    UNSUPPORTED_OPERATION(3004, "지원하지 않는 요청입니다", "Unsupported operation"),
    INVALID_LENGTH(3005, "값의 길이는 %d자 이상, %d자 이하여야 합니다", "The value must be at least %d characters and at most %d characters"),
    INVALID_FORMAT(3006, "형식이 잘못되었습니다", "Invalid format"),
    INVALID_VALUE_LENGTH(3005, "%s 길이는 %d자 이상, %d자 이하여야 합니다", "%s must be at least %d characters and at most %d characters"),
    ACCESS_DENIED(3007, "접근 권한이 없습니다", "Access denied"),


    // 회원 관련(3100~)
    DUPLICATE_MEMBER(3100, "이미 존재하는 회원입니다", "Duplicate Member"),
    MEMBER_NOT_FOUND(3101, "회원 정보를 찾을 수 없습니다", "Member Not Found"),
    INVALID_EMAIL_FORMAT(3102, "유효하지 않은 이메일 형식입니다", "Invalid email format"),
    DUPLICATE_EMAIL(3103, "이미 존재하는 이메일입니다", "Duplicate Email"),
    DUPLICATE_NICKNAME(3104, "이미 존재하는 닉네임입니다", "Duplicate Nickname"),
    INVALID_PASSWORD(3105, "패스워드가 일치하지 않습니다", "Password does not match"),
    INVALID_PASSWORD_ERROR(3106, "비밀번호 오류입니다", "Password error"),
    EMAIL_NOT_FOUND(3107, "없는 이메일입니다", "Email not found"),
    AUTH_CODE_EXPIRED(3108, "인증 코드가 만료되었습니다", "Authentication code expired"),
    AUTH_CODE_INVALID(3109, "인증 코드가 잘못되었습니다", "Invalid authentication code"),
    GENDER_CHECK_REQUIRED(3110, "성별을 확인해 주세요", "Please check the gender"),
    VERIFICATION_CODE_NOT_FOUND(3112, "인증 코드를 찾을 수 없습니다", "Verification code not found"),
    ALREADY_VERIFIED(3113, "이미 인증된 코드입니다", "Code already verified"),
    NEED_VERIFICATION(3114, "인증이 필요한 회원입니다", "Verification required"),



    // 펫관련(3200~)
    PET_NOT_FOUND(3200, "펫을 찾을 수 없습니다", "Pet Not Found"),
    // 내 소유 펫 아님
    PET_NOT_OWNED(3201, "해당 펫은 내 소유가 아닙니다", "This pet is not owned by me"),

    // 투표관련 (3300~)
    VOTE_NOT_FOUND(3300, "투표를 찾을 수 없습니다", "Vote Not Found"),
    // 이번달 이미 등록된 투표
    VOTE_ALREADY_REGISTERED(3301, "이번 달에 이미 등록된 투표입니다", "Vote already registered for this month"),
    DUPLICATE_VOTE(3303, "이미 존재하는 투표입니다", "Duplicate Vote"),
    // 투표가 등록되어있어 삭제를 못한다
    VOTE_CANNOT_DELETE(3302, "투표가 등록되어 있어 삭제할 수 없습니다", "Cannot delete vote as it is already registered"),
    // 오늘 이미 투표했음
    ALREADY_VOTED_TODAY(3305, "오늘 이미 투표하셨습니다", "Already voted today"),
    // 주 1회 투표 등록 제한
    WEEKLY_VOTE_REGISTRATION_LIMIT_EXCEEDED(3304, "주 1회만 투표를 등록할 수 있습니다", "You can only register one vote per week"),
    // 비회원 이메일 미인증
    EMAIL_NOT_VERIFIED_TODAY(3306, "오늘 인증되지 않은 이메일입니다", "Email not verified today"),
    // 투표 이메일 인증 관련
    EMAIL_ALREADY_REGISTERED(3307, "이미 등록된 회원 이메일입니다", "Email already registered as member"),
    EMAIL_ALREADY_VERIFIED_TODAY(3308, "오늘 이미 인증된 이메일입니다", "Email already verified today"),
    INVALID_VERIFICATION_TOKEN(3309, "유효하지 않은 인증 토큰입니다", "Invalid verification token"),
    VERIFICATION_TOKEN_EXPIRED(3310, "인증 토큰이 만료되었습니다", "Verification token expired"),

    // 추가 비즈니스 코드들
    INVALID_PHONE_NUMBER(3111, "잘못된 전화번호 형식입니다", "Invalid phone number format"),
    INVALID_WEIGHT_RANGE(3202, "몸무게는 0.1kg 이상 200kg 이하여야 합니다", "Weight must be between 0.1kg and 200kg"),
    INVALID_HEIGHT_RANGE(3203, "키는 1cm 이상 200cm 이하여야 합니다", "Height must be between 1cm and 200cm"),
    INVALID_BIRTH_DATE(3204, "생년월일이 잘못되었습니다", "Invalid birth date"),
    INVALID_PET_OWNER(3205, "펫 소유자가 일치하지 않습니다", "Pet owner does not match"),
    INVALID_USER_UPDATE(3112, "사용자 업데이트가 잘못되었습니다", "Invalid user update"),
    INVALID_VOTE_MONTH(3304, "잘못된 투표 월입니다", "Invalid vote month"),

    // 투표 카운트 관련 (3311~)
    VOTE_COUNT_INSUFFICIENT(3311, "투표 카운트가 부족합니다", "Insufficient vote count"),
    INVALID_VOTE_COUNT_STATE(3312, "잘못된 투표 카운트 상태입니다", "Invalid vote count state"),
    VOTE_COUNT_LIMIT_EXCEEDED(3313, "투표 카운트 한도를 초과했습니다", "Vote count limit exceeded"),

    // 공지사항 관련 (3400~)
    NOTICE_NOT_FOUND(3400, "공지사항을 찾을 수 없습니다", "Notice Not Found"),
    INVALID_DATE_RANGE(3401, "시작일이 종료일보다 늦을 수 없습니다", "Start date cannot be later than end date"),

    // 이벤트 관련 (3500~)
    EVENT_NOT_FOUND(3500, "이벤트를 찾을 수 없습니다", "Event Not Found"),

    // 커뮤니티 관련 (3600~)
    POST_NOT_FOUND(3600, "게시글을 찾을 수 없습니다", "Post Not Found"),
    COMMENT_NOT_FOUND(3601, "댓글을 찾을 수 없습니다", "Comment Not Found"),
    INVALID_COMMENT_DEPTH(3602, "대댓글의 대댓글은 작성할 수 없습니다", "Cannot create reply to reply");






    private final int code;
    private final String messageKo;
    private final String messageEn;

    // 한국어 메시지와 영어 메시지를 포맷팅해서 반환하는 메서드
    public String getFormattedMessageKo(int min, int max) {
        return String.format(this.messageKo, min, max);
    }
    public String getFormattedMessageKo(String koName, int min, int max) {
        return String.format(this.messageKo,koName, min, max);
    }

    public String getFormattedMessageEn(int min, int max) {
        return String.format(this.messageEn, min, max);
    }
    public String getFormattedMessageEn(String enName, int min, int max) {
        return String.format(this.messageEn,enName, min, max);
    }






}

