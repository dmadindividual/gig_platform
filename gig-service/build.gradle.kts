dependencies {
    implementation(project(":shared-kernel"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Elasticsearch for search
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    // Kafka for events
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:elasticsearch")
}


// ADD THIS
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}