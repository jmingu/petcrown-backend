package kr.co.common.util;

import kr.co.common.exception.PetCrownException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import static kr.co.common.enums.BusinessCode.MISSING_REQUIRED_VALUE;

public class CryptoUtil {

    private static final SecureRandom random = new SecureRandom();

    // 암호화
    public static String encrypt(String value, String key) throws Exception {
        if(value == null || key == null){
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }


    // 복호화
    public static String decrypt(String value, String key) throws Exception {
        if(value == null || key == null){
            throw new PetCrownException(MISSING_REQUIRED_VALUE);
        }

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(value));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    /**
     * 6자리 이메일 인증 코드 생성
     */
    public static String generateVerificationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 임시 비밀번호 생성 (영문 대소문자, 숫자 조합 10자리)
     */
    public static String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
