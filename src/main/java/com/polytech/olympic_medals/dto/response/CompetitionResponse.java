package com.polytech.olympic_medals.dto.response;

import com.polytech.olympic_medals.model.StatutCompetition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionResponse {

    private Long id;
    private String nom;
    private String discipline;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutCompetition statut;
}