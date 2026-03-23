package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.CompetitionRequest;
import com.polytech.olympic_medals.dto.response.CompetitionResponse;

import java.util.List;

public interface CompetitionService {

    CompetitionResponse creerCompetition(CompetitionRequest request);

    CompetitionResponse obtenirCompetitionParId(Long id);

    List<CompetitionResponse> obtenirToutesLesCompetitions();

    CompetitionResponse modifierCompetition(Long id, CompetitionRequest request);

    void supprimerCompetition(Long id);
}