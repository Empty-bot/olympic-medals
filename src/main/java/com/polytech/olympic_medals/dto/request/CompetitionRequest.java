package com.polytech.olympic_medals.dto.request;

import com.polytech.olympic_medals.model.StatutCompetition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionRequest {

    @NotBlank(message = "Le nom de la compétition est obligatoire")
    private String nom;

    @NotBlank(message = "La discipline est obligatoire")
    private String discipline;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    private StatutCompetition statut;
}