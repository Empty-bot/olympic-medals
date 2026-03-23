package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.PaysRequest;
import com.polytech.olympic_medals.dto.response.PaysResponse;

import java.util.List;

public interface PaysService {

    PaysResponse creerPays(PaysRequest request);

    PaysResponse obtenirPaysParId(Long id);

    List<PaysResponse> obtenirTousLesPays();

    PaysResponse modifierPays(Long id, PaysRequest request);

    void supprimerPays(Long id);
}