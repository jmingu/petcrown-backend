package kr.co.api.healthcheck.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.common.contoller.BaseController;
import kr.co.common.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HealthCheck", description = "헬스체크 API")
public class HealthCheckRestController extends BaseController {

    @AuthRequired(authSkip = true)
    @Operation(summary = "헬스체크 확인", description = "헬스체크 확인")
    @GetMapping
    public ResponseEntity<CommonResponseDto> healthCheck() {

        return success();
    }
}
