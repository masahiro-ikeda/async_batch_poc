plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "async-batch-poc"

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project("project-consumer"))
    implementation(project("project-producer"))
    implementation(project("project-queue"))
}