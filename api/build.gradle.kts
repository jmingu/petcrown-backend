import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.Logging

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    mainClass.set("kr.co.api.PetCrownApiApplication")
}

tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":common"))

    implementation ("org.springframework.boot:spring-boot-starter-mail")

    implementation("org.springframework.boot:spring-boot-starter-security")
    // jsonwebtoken(okta), java-jwt는 oath0에서 만들었다
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // JOOQ code generation
    jooqGenerator("org.postgresql:postgresql")
}

jooq {
    // Spring Boot 3.4.3이 사용하는 JOOQ 버전
    version.set("3.19.19")
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN

                // 환경 선택: -Pjooq.env=prod 또는 gradle.properties의 jooq.env 값 사용
                val env: String = project.findProperty("jooq.env")?.toString() ?: "dev"
                println("JOOQ Code Generation Environment: ${env}")

                jdbc.apply {
                    driver = project.findProperty("jooq.${env}.driver")?.toString() ?: "org.postgresql.Driver"
                    url = project.findProperty("jooq.${env}.url")?.toString() ?: "jdbc:postgresql://localhost:5432/petcrown_dev"
                    user = project.findProperty("jooq.${env}.user")?.toString() ?: "postgres"
                    password = project.findProperty("jooq.${env}.password")?.toString() ?: ""
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = "flyway_schema_history|databasechangelog|databasechangeloglock"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isFluentSetters = true
                        isPojosAsJavaRecordClasses = false
                    }
                    target.apply {
                        packageName = "kr.co.common.jooq"
                        directory = "common/src/main/java"
                    }
                }
            }
        }
    }
}
