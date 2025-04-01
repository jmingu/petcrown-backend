package kr.co.api.adapter.in.web.healthcheck;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.api.common.annotation.AuthRequired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
@Tag(name = "healthcheck", description = "healthcheck 관련 API")
public class HealthCheckRestController {


    @AuthRequired(authSkip = true)
    @GetMapping("/ok")
    @Operation(summary = "healthcheck ok", description = "healthcheck ok")
    public String livenessProbe() {
        return "ok";
    }


}
