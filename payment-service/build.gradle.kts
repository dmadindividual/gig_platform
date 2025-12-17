dependencies {
    implementation(project(":shared-kernel"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Stripe SDK
    implementation("com.stripe:stripe-java:24.3.0")

    // Kafka for events
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.testcontainers:postgresql")
}


// ADD THIS
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}