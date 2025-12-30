package com.gigplatform.user.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter userRegistrationCounter(MeterRegistry registry) {
        return Counter.builder("user.registrations.total")
                .description("Total number of user registrations")
                .tag("service", "user-service")
                .register(registry);
    }

    @Bean
    public Counter loginSuccessCounter(MeterRegistry registry) {
        return Counter.builder("user.login.success")
                .description("Successful login attempts")
                .tag("service", "user-service")
                .register(registry);
    }

    @Bean
    public Counter loginFailureCounter(MeterRegistry registry) {
        return Counter.builder("user.login.failure")
                .description("Failed login attempts")
                .tag("service", "user-service")
                .register(registry);
    }
}