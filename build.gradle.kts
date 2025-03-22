plugins {
	id("java")
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

allprojects {
	group = "kr.co.petcrown"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}


configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

subprojects {
	apply {
		plugin("java")
		plugin("java-library")
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
	}

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	dependencies {


		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.springframework.boot:spring-boot-starter-data-jpa")

		compileOnly("org.projectlombok:lombok")
		annotationProcessor("org.projectlombok:lombok")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.springframework.security:spring-security-test")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
		annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	}

	dependencyManagement {
		imports {
//			mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}

//dependencies {
////	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	//	runtimeOnly("org.postgresql:postgresql")
//	implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("org.springframework.boot:spring-boot-starter-web")
//
//
//
//
//
//}


