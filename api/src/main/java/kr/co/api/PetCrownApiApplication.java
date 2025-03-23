package kr.co.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "kr.co.api.adapter.out.persistence.repository")
@EntityScan(basePackages = "kr.co.common.entity")
public class PetCrownApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetCrownApiApplication.class, args);
    }
}