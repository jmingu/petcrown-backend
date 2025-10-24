package kr.co.api.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.common.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 민감한 정보 암호화를 위한 임시 컨트롤러
 * 
 * 사용법:
 * 1. 환경변수 JASYPT_PASSWORD 설정
 * 2. 애플리케이션 실행
 * 3. 원하는 엔드포인트 호출
 * 4. 결과를 application.yaml에 복사
 * 5. 이 컨트롤러 삭제 (보안상 중요!)
 * 
 * 엔드포인트:
 * - GET /encrypt?value=값                           (단일 암호화)
 * - GET /encrypt?key=키이름&value=값                (키와 함께 단일 암호화)
 * - GET /encrypt/multi?key1=value1&key2=value2     (동적 다중 암호화)
 * - POST /encrypt/batch (JSON Map으로 배치)         (★ 추천: 키-값 쌍으로)
 * - POST /encrypt/values (JSON Array로 값만)       (값만 있을 때)
 * - GET /encrypt/all                               (미리 정의된 모든 값)
 */
@Tag(name = "암호화", description = "Jasypt 암호화 API (개발용)")
@RestController
@RequestMapping("/encrypt")
@RequiredArgsConstructor
@Slf4j
public class EncryptionController {

    private final EncryptionUtil encryptionUtil;

    @Operation(
        summary = "단일 값 암호화",
        description = "개별 값을 Jasypt로 암호화합니다. 개발환경에서만 사용하세요."
    )
    @ApiResponse(
        responseCode = "200",
        description = "암호화 성공",
        content = @Content(schema = @Schema(type = "string"))
    )
    @AuthRequired(authSkip = true)
    @GetMapping
    public String encryptValue(
            @Parameter(description = "암호화할 값", example = "test@example.com", required = true)
            @RequestParam String value) {
        
        String encrypted = encryptionUtil.encryptForConfig(value);
        
        System.out.println("\n=== 암호화 결과 ===");
        System.out.println("==================\n");
        
        log.info("Encrypted {}: {}", encrypted);
        
        return String.format("암호화 완료!<br>%s<br>콘솔도 확인해보세요!", encrypted);
    }
    
    @Operation(
        summary = "배치 암호화 (키-값 쌍)",
        description = "여러 설정값을 JSON Map 형태로 한 번에 암호화합니다. 가장 편리한 방법입니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "배치 암호화 성공",
        content = @Content(
            schema = @Schema(type = "object"),
            examples = @ExampleObject(
                name = "암호화 결과 예시",
                value = """
                {
                  "mail-username": "ENC(암호화된값1)",
                  "mail-password": "ENC(암호화된값2)"
                }
                """
            )
        )
    )
    @AuthRequired(authSkip = true)
    @PostMapping("/batch")
    public Map<String, String> encryptBatch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "암호화할 키-값 쌍들",
                required = true,
                content = @Content(
                    examples = @ExampleObject(
                        name = "요청 예시",
                        value = """
                        {
                          "mail-username": "test@gmail.com",
                          "mail-password": "ssss aaaa vvvv fdfd",
                          "jwt-secret-key": "jwt_secret_key"
                        }
                        """
                    )
                )
            )
            @RequestBody Map<String, String> values) {
        
        if (values.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "JSON 바디가 비어있습니다. 예시: {\"key1\": \"value1\", \"key2\": \"value2\"}");
            return error;
        }
        
        Map<String, String> encryptedResults = new HashMap<>();
        
        System.out.println("\n=== 배치 암호화 결과 ===");
        
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String encrypted = encryptionUtil.encryptForConfig(value);
            
            encryptedResults.put(key, encrypted);
            
            System.out.println(key + ": " + encrypted);
            log.info("Encrypted {}: {}", key, encrypted);
        }
        
        System.out.println("========================\n");
        
        return encryptedResults;
    }



}