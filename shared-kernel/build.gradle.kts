plugins {
    `java-library`
}

dependencies {
    // Jackson for JSON serialization
    api("com.fasterxml.jackson.core:jackson-databind")

    // Validation API
    api("jakarta.validation:jakarta.validation-api")

    // JPA annotations (needed for @Entity, @Embeddable, etc.)
    api("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // Spring Data JPA (for @CreatedDate, @LastModifiedDate, AuditingEntityListener)
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}