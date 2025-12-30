package com.gigplatform.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "message", "User service is temporarily unavailable. Please try again later.",
                        "errorCode", "SERVICE_UNAVAILABLE",
                        "timestamp", Instant.now()
                ));
    }

    @GetMapping("/worker-service")
    public ResponseEntity<Map<String, Object>> workerServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "message", "Worker service is temporarily unavailable. Please try again later.",
                        "errorCode", "SERVICE_UNAVAILABLE",
                        "timestamp", Instant.now()
                ));
    }

    @GetMapping("/gig-service")
    public ResponseEntity<Map<String, Object>> gigServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "message", "Gig service is temporarily unavailable. Please try again later.",
                        "errorCode", "SERVICE_UNAVAILABLE",
                        "timestamp", Instant.now()
                ));
    }

    @GetMapping("/payment-service")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "message", "Payment service is temporarily unavailable. Please try again later.",
                        "errorCode", "SERVICE_UNAVAILABLE",
                        "timestamp", Instant.now()
                ));
    }
}