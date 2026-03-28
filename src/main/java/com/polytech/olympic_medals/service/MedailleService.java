package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.MedailleRequest;
import com.polytech.olympic_medals.dto.response.ClassementResponse;
import com.polytech.olympic_medals.dto.response.MedailleResponse;
import com.polytech.olympic_medals.dto.response.PageResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedailleService {

    MedailleResponse enregistrerMedaille(MedailleRequest request);

    MedailleResponse obtenirMedailleParId(Long id);

    List<MedailleResponse> obtenirToutesLesMedailles();

    List<MedailleResponse> obtenirMedaillesParAthlete(Long athleteId);

    List<MedailleResponse> obtenirMedaillesParCompetition(Long competitionId);

    List<ClassementResponse> obtenirClassement(String tri);

    ClassementResponse obtenirStatsPays(Long paysId);

    PageResponse<MedailleResponse> obtenirToutesLesMedaillesPageable(Pageable pageable);
}