package com.example.customer_api.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String key = request.getRemoteAddr();
        Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            return true;
        }

        response.setStatus(429);
        response.getWriter().write("Too many requests");
        return false;
    }

    private Bucket createNewBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1)))
            .build();
    }
}
