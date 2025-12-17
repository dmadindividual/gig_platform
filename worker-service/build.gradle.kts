dependencies {
    implementation(project(":shared-kernel"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Kafka for event consumption
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}


// ADD THIS
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}