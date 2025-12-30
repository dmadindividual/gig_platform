package com.gigplatform.user.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String USER_EVENTS_TOPIC = "user-events";

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder
                .name(USER_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)  // Single replica (for local dev)
                .config("retention.ms", "604800000")  // 7 days
                .config("compression.type", "snappy")
                .build();
    }
}
