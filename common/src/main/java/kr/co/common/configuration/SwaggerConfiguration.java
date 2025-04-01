package kr.co.common.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCrown API")
                        .version("1.0.0")
                        .description("반려동물 얼짱 순위 사이트 API"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // 보안 요구 사항 추가
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // HTTP 인증 방식
                                .scheme("bearer") // Bearer 스킴
                                .bearerFormat("JWT"))); // JWT 형식
    }

    @Bean
    public GroupedOpenApi group() {
        return GroupedOpenApi.builder()
                .group("healthcheck")
                .packagesToScan("kr.co.api.adapter.in.web.healthcheck")
                .build();
    }
    @Bean
    public GroupedOpenApi group1() {
        return GroupedOpenApi.builder()
                .group("유저관련")
                .packagesToScan("kr.co.api.adapter.in.web.user")
//                .pathsToMatch("/demo/**")
                .build();
    }

    @Bean
    public GroupedOpenApi group2() {
        return GroupedOpenApi.builder()
                .group("펫관련")
                .packagesToScan("kr.co.api.adapter.in.web.pet")
                .build();
    }
}
