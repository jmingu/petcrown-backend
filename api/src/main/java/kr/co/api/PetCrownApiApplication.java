package kr.co.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"kr.co.api", "kr.co.common"}) // 스캔이 안된다 common쪽
//@EntityScan(basePackages = "kr.co.common.entity")
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = {"kr.co.api"})
public class PetCrownApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetCrownApiApplication.class, args);
    }
}