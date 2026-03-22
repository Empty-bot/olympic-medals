package com.polytech.olympic_medals.dto.response;

import com.polytech.olympic_medals.model.TypeMedaille;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedailleResponse {

    private Long id;
    private TypeMedaille type;
    private LocalDate dateObtention;
    private AthleteResponse athlete;
    private PaysResponse pays;
    private CompetitionResponse competition;
}