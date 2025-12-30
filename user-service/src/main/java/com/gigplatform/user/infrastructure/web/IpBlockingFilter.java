package com.gigplatform.user.infrastructure.web;

import com.gigplatform.user.infrastructure.security.IpBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)  // Run early, after CorrelationIdFilter
@RequiredArgsConstructor
@Slf4j
public class IpBlockingFilter extends OncePerRequestFilter {

    private final IpBlacklistService ipBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String ipAddress = getClientIp(request);

        // Check whitelist first (whitelisted IPs bypass all checks)
        if (ipBlacklistService.isWhitelisted(ipAddress)) {
            log.debug("Whitelisted IP accessing: {}", ipAddress);
            filterChain.doFilter(request, response);
            return;
        }

        // Check blacklist
        if (ipBlacklistService.isBlocked(ipAddress)) {
            log.warn("Blocked IP attempting access: {} from {}", ipAddress, request.getRequestURI());

            Long remainingSeconds = ipBlacklistService.getBlockTimeRemaining(ipAddress);
            String message = remainingSeconds != null && remainingSeconds > 0
                    ? String.format("Access denied. Your IP is blocked for %d more minutes.", remainingSeconds / 60)
                    : "Access denied. Your IP address has been permanently blocked.";

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                    "{\"success\":false,\"message\":\"%s\",\"errorCode\":\"IP_BLOCKED\"}",
                    message
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Don't block actuator health checks
        return path.startsWith("/actuator/health");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }
}