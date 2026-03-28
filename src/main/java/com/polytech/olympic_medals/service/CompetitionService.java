package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.CompetitionRequest;
import com.polytech.olympic_medals.dto.response.CompetitionResponse;
import com.polytech.olympic_medals.dto.response.PageResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompetitionService {

    CompetitionResponse creerCompetition(CompetitionRequest request);

    CompetitionResponse obtenirCompetitionParId(Long id);

    List<CompetitionResponse> obtenirToutesLesCompetitions();

    CompetitionResponse modifierCompetition(Long id, CompetitionRequest request);

    PageResponse<CompetitionResponse> obtenirToutesLesCompetitionsPageable(Pageable pageable);

    void supprimerCompetition(Long id);
}