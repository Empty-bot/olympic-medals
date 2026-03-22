package com.polytech.olympic_medals.dto.request;

import com.polytech.olympic_medals.model.TypeMedaille;
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
public class MedailleRequest {

    @NotNull(message = "Le type de médaille est obligatoire")
    private TypeMedaille type;

    private LocalDate dateObtention;

    @NotNull(message = "L'identifiant de l'athlète est obligatoire")
    private Long athleteId;

    @NotNull(message = "L'identifiant du pays est obligatoire")
    private Long paysId;

    @NotNull(message = "L'identifiant de la compétition est obligatoire")
    private Long competitionId;
}