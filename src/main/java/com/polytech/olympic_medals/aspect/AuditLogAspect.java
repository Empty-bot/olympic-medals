package com.polytech.olympic_medals.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {

    // Logger nommé "AUDIT", correspond au logger configuré dans logback-spring.xml
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT");

    /**
     * Pointcut : cible toutes les méthodes de toutes les classes
     * du package controller. C'est ici qu'on définit le "où".
     */
    @Pointcut("execution(* com.polytech.olympic_medals.controller..*(..))")
    public void tousLesControllers() {
        // Méthode vide — elle sert uniquement de point de référence nommé
        // pour le Pointcut, qu'on réutilise ensuite dans l'Advice ci-dessous.
    }

    /**
     * Advice : s'exécute AVANT chaque méthode ciblée par le Pointcut.
     * C'est ici qu'on définit le "quoi" et le "quand".
     */
    @Before("tousLesControllers()")
    public void auditerRequete(JoinPoint joinPoint) {
        HttpServletRequest request = obtenirRequeteHttp();

        AuditLogEntry entree = AuditLogEntry.builder()
                .dateHeure(LocalDateTime.now())
                .user(obtenirUser(request))
                .adresseIp(obtenirIpClient(request))
                .motDePasse(PasswordMasker.extraireEtMasquer(joinPoint.getArgs()))
                .endpoint(obtenirEndpoint(request, joinPoint))
                .build();

        AUDIT_LOGGER.info(entree.toString());
    }

    /**
     * Récupère la requête HTTP courante depuis le contexte Spring.
     * RequestContextHolder donne accès à la requête HTTP en cours,
     * même depuis une classe qui n'est pas un Controller.
     */
    private HttpServletRequest obtenirRequeteHttp() {
        ServletRequestAttributes attributs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributs != null ? attributs.getRequest() : null;
    }

    private String obtenirUser(HttpServletRequest request) {
        // Pas d'authentification dans le projet actuel.
        // Champ prévu pour une future intégration de Spring Security :
        // on lirait ici le principal authentifié (ex: SecurityContextHolder).
        if (request != null) {
            String userHeader = request.getHeader("X-User");
            if (userHeader != null && !userHeader.isBlank()) {
                return userHeader;
            }
        }
        return "ANONYME";
    }

    private String obtenirIpClient(HttpServletRequest request) {
        if (request == null) {
            return "INCONNU";
        }
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String obtenirEndpoint(HttpServletRequest request, JoinPoint joinPoint) {
        if (request != null) {
            return request.getMethod() + " " + request.getRequestURI();
        }
        // Repli si la requête HTTP n'est pas accessible : on utilise
        // la signature de la méthode interceptée.
        return joinPoint.getSignature().toShortString();
    }
}