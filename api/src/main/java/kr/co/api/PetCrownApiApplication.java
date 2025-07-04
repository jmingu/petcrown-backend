package kr.co.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"kr.co.api", "kr.co.common"}) // 스캔이 안된다 common쪽
@EnableJpaRepositories(basePackages = "kr.co.api.adapter.out.persistence.repository")
@EntityScan(basePackages = "kr.co.common.entity")
@EnableAsync
@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = {"kr.co.api"})
public class PetCrownApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetCrownApiApplication.class, args);
    }
}