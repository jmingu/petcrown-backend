plugins {
	id("java")
	id("org.springframework.boot") version "3.4.3" apply false
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "9.0" apply false
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

allprojects {
	group = "kr.co.petcrown"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}


configurations.named("compileOnly") {
	extendsFrom(configurations.named("annotationProcessor").get())
}

subprojects { // 각 모듈에 적용할 공통 설정
	apply {
		plugin("java")
		plugin("java-library")
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("nu.studer.jooq")
	}

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	dependencies {
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")


		implementation("org.springframework.boot:spring-boot-starter-web")

		// JOOQ (MyBatis 제거됨)
		implementation("org.springframework.boot:spring-boot-starter-jooq")
		runtimeOnly("org.postgresql:postgresql")
		
		// 네이버 클라우드 Object Storage
		implementation("com.amazonaws:aws-java-sdk-s3:1.12.618")
		
		// Jasypt (암호화)
		implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
		
		// .env 파일 지원
		implementation("me.paulschwarz:spring-dotenv:4.0.0")

		compileOnly("org.projectlombok:lombok")
		annotationProcessor("org.projectlombok:lombok")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.springframework.security:spring-security-test")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
		annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	}

//	// 모든 서브 프로젝트에서 'org.springframework.boot:spring-boot-starter-data-jpa'를 사용하되, 'subProjectA'에서는 사용하지 않고 싶다
//	project(':gateway') {
//		configurations {
//			all*.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-data-jpa'
//		}
//	}


	dependencyManagement {
		imports {
//			mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}



