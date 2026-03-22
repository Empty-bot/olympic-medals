package com.polytech.olympic_medals.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaysRequest {

    @NotBlank(message = "Le code pays est obligatoire")
    @Size(min = 2, max = 3, message = "Le code pays doit contenir 2 ou 3 caractères")
    private String code;

    @NotBlank(message = "Le nom du pays est obligatoire")
    private String nom;

    private String drapeau;
}