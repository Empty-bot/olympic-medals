package com.polytech.olympic_medals.aspect;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class AuditLogEntry {

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalDateTime dateHeure;
    private String user;
    private String adresseIp;
    private String motDePasse;
    private String endpoint;

    @Override
    public String toString() {
        return String.format(
                "[%s] user=%s | ip=%s | password=%s | endpoint=%s",
                dateHeure.format(FORMAT_DATE),
                user,
                adresseIp,
                motDePasse,
                endpoint
        );
    }
}