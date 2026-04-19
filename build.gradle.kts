plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "jp.i432kg"
version = "0.0.1-SNAPSHOT"
description = "Anonymous SNS for photo sharing on map"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    dependencies {
        // 脆弱性対応のため Spring Boot 管理依存を個別上書きしている。
        // Spring Boot 更新時は各 override を一時的に外し、
        // `./gradlew dependencyInsight --dependency <module>` で安全版へ自然解決されるか確認すること。
        // override を外した状態で `./gradlew test` が通るなら、その override は削除してよい。
        dependency("org.thymeleaf:thymeleaf:3.1.4.RELEASE")
        dependency("org.thymeleaf:thymeleaf-spring6:3.1.4.RELEASE")

        dependencySet("org.apache.tomcat.embed:11.0.21") {
            entry("tomcat-embed-core")
            entry("tomcat-embed-el")
            entry("tomcat-embed-websocket")
        }

        dependencySet("tools.jackson.core:3.1.1") {
            entry("jackson-core")
            entry("jackson-databind")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.0")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("com.github.f4b6a3:ulid-creator:5.2.3")
    implementation("com.drewnoakes:metadata-extractor:2.19.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")
    implementation(platform("software.amazon.awssdk:bom:2.31.20"))
    implementation("software.amazon.awssdk:s3")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-mysql")

    compileOnly("org.projectlombok:lombok")
    compileOnly("org.jspecify:jspecify:1.0.0")

    runtimeOnly("com.mysql:mysql-connector-j")

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testCompileOnly("org.jspecify:jspecify:1.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:4.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
