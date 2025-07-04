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

    // 펫관련(3200~)
    PET_NOT_FOUND(3200, "펫을 찾을 수 없습니다", "Pet Not Found"),
    // 내 소유 펫 아님
    PET_NOT_OWNED(3201, "해당 펫은 내 소유가 아닙니다", "This pet is not owned by me");





    private final int code;
    private final String messageKo;
    private final String messageEn;

    // 한국어 메시지와 영어 메시지를 포맷팅해서 반환하는 메서드
    public String getFormattedMessageKo(int min, int max) {
        return String.format(this.messageKo, min, max);
    }

    public String getFormattedMessageEn(int min, int max) {
        return String.format(this.messageEn, min, max);
    }






}

