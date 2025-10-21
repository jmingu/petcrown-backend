package kr.co.common.util;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Jasypt를 이용한 암호화/복호화 유틸리티
 * 
 * 사용법:
 * 1. 민감한 정보 암호화: String encrypted = encryptionUtil.encrypt("민감한정보");
 * 2. application.yaml에 추가: my-secret: ENC(암호화된문자열)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptionUtil {

    private final StringEncryptor jasyptStringEncryptor;

    /**
     * 문자열 암호화
     * 
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            throw new IllegalArgumentException("암호화할 문자열이 비어있습니다.");
        }
        
        try {
            String encrypted = jasyptStringEncryptor.encrypt(plainText);
            log.info("암호화 완료 - 원본 길이: {}, 암호화 길이: {}", plainText.length(), encrypted.length());
            return encrypted;
        } catch (Exception e) {
            log.error("암호화 실패: {}", e.getMessage());
            throw new RuntimeException("암호화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 문자열 복호화
     * 
     * @param encryptedText 복호화할 암호문
     * @return 복호화된 평문
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.trim().isEmpty()) {
            throw new IllegalArgumentException("복호화할 문자열이 비어있습니다.");
        }
        
        try {
            String decrypted = jasyptStringEncryptor.decrypt(encryptedText);
            log.info("복호화 완료 - 암호문 길이: {}, 복호화 길이: {}", encryptedText.length(), decrypted.length());
            return decrypted;
        } catch (Exception e) {
            log.error("복호화 실패: {}", e.getMessage());
            throw new RuntimeException("복호화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 암호화된 문자열을 Spring Boot 설정 형식으로 반환
     * 
     * @param plainText 암호화할 평문
     * @return ENC(암호화된문자열) 형태
     */
    public String encryptForConfig(String plainText) {
        String encrypted = encrypt(plainText);
        return "ENC(" + encrypted + ")";
    }

    /**
     * 여러 문자열을 한번에 암호화하여 로그로 출력
     * 개발 시 편의를 위한 메서드
     * 
     * @param keyValuePairs 키-값 쌍 (예: "db-password", "mypassword123")
     */
    public void encryptMultiple(String... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("키-값 쌍이 맞지 않습니다. 짝수 개의 인자를 제공하세요.");
        }

        log.info("=== 암호화 결과 ===");
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            String value = keyValuePairs[i + 1];
            String encrypted = encryptForConfig(value);
            
            log.info("{}: {}", key, encrypted);
            System.out.println(key + ": " + encrypted);
        }
        log.info("==================");
    }
}