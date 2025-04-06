package kr.co.api.adapter.in.web.healthcheck;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import kr.co.common.contoller.BaseController;
import kr.co.common.entity.common.CommonResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
@Tag(name = "healthCheck", description = "healthCheck 관련 API")
public class HealthCheckRestController extends BaseController {


    @AuthRequired(authSkip = true)
    @GetMapping("/ok")
    @Operation(summary = "healthCheck Ok", description = "healthCheck Ok")
    public ResponseEntity<CommonResponseDto> healthCheck() {
        return success();
    }


}
