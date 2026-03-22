package com.polytech.olympic_medals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AthleteResponse {

    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String discipline;
    private PaysResponse pays;
}