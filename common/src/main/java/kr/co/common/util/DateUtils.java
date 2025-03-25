package kr.co.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtils {

    public static LocalDate convertToLocalDate(String dateStr, String pattern) {
        // 구분자 제거 (대시, 마침표 등)
        String cleanedDate = dateStr.replaceAll("[^0-9]", "");

        // 날짜 포맷 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // 문자열을 LocalDate로 변환
        return LocalDate.parse(cleanedDate, formatter);
    }

}
