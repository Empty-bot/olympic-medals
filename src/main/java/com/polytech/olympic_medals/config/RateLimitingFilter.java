package com.polytech.olympic_medals.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    // Un seau par adresse IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Limite : 100 requêtes par minute par IP
    private static final int CAPACITE = 100;
    private static final int RECHARGE_PAR_MINUTE = 100;

    private Bucket obtenirOuCreerBucket(String ip) {
        return buckets.computeIfAbsent(ip, cle -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(CAPACITE)
                        .refillGreedy(RECHARGE_PAR_MINUTE, Duration.ofMinutes(1))
                        .build())
                .build()
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = obtenirIpClient(request);
        Bucket bucket = obtenirOuCreerBucket(ip);

        if (bucket.tryConsume(1)) {
            // Jeton disponible — on ajoute les headers informatifs
            response.addHeader("X-RateLimit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Seau vide — on rejette la requête
            log.warn("Rate limit dépassé pour l'IP : {}", ip);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                {
                    "success": false,
                    "message": "Trop de requêtes. Veuillez réessayer dans une minute.",
                    "timestamp": "%s"
                }
                """.formatted(java.time.LocalDateTime.now()));
        }
    }

    private String obtenirIpClient(HttpServletRequest request) {
        // Récupère l'IP réelle si derrière un proxy (ex: nginx)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}