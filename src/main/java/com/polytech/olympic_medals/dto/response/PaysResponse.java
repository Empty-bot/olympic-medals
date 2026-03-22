package com.polytech.olympic_medals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaysResponse {

    private Long id;
    private String code;
    private String nom;
    private String drapeau;
}