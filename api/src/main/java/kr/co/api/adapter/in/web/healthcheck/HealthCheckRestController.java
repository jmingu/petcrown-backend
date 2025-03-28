package kr.co.api.adapter.in.web.healthcheck;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckRestController {


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/ready")
    public String readinessProbe() {
        return "ready";
    }

    @GetMapping("/ok")
    public String livenessProbe() {
        return "ok";
    }


}
