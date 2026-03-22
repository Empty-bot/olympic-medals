package com.polytech.olympic_medals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassementResponse {

    private int rang;
    private Long paysId;
    private String paysNom;
    private String paysCode;
    private String paysDrapeau;
    private long nbOr;
    private long nbArgent;
    private long nbBronze;
    private long total;
    private long points;
}