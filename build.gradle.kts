plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
    id("org.jetbrains.kotlin.kapt") version "2.2.21"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "top.inept"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

val querydslVersion = "7.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    testImplementation("org.springframework.security:spring-security-test")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //openapi
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("commons-io:commons-io:2.16.1")

    //querydsl
    implementation("io.github.openfeign.querydsl:querydsl-jpa-spring:$querydslVersion")
    kapt("io.github.openfeign.querydsl:querydsl-apt:$querydslVersion:jakarta")

    //minio
    implementation("io.minio:minio:8.6.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openApi {
    apiDocsUrl = "http://localhost:8080/v3/api-docs.yaml"
    outputFileName = "openapi.yaml"
}