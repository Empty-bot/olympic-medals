package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.AthleteRequest;
import com.polytech.olympic_medals.dto.response.AthleteResponse;
import com.polytech.olympic_medals.dto.response.PageResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AthleteService {

    AthleteResponse creerAthlete(AthleteRequest request);

    AthleteResponse obtenirAthleteParId(Long id);

    List<AthleteResponse> obtenirTousLesAthletes();

    List<AthleteResponse> obtenirAthleteParPays(Long paysId);

    AthleteResponse modifierAthlete(Long id, AthleteRequest request);

    PageResponse<AthleteResponse> obtenirTousLesAthletesPageable(Pageable pageable);

    void supprimerAthlete(Long id);
}