plugins {
	java
	id("org.springframework.boot") version "4.0.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "org.dehorsed"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(26)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring boot starters
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Mapstruct
    implementation("org.mapstruct:mapstruct:1.+")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.+")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.+")

	// Integration tests with Testcontainers
	testImplementation("org.junit.jupiter:junit-jupiter:5.+")
	testImplementation("org.testcontainers:testcontainers:2.+")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.+")
	testImplementation("org.testcontainers:testcontainers-mongodb:2.+")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

	// Spring boot test starters
	testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

	// JUnit
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Lombok test packages
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	// REST API for integration tests
	testImplementation("io.rest-assured:rest-assured:6.+")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
