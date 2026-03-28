package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.PaysRequest;
import com.polytech.olympic_medals.dto.response.PageResponse;
import com.polytech.olympic_medals.dto.response.PaysResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaysService {

    PaysResponse creerPays(PaysRequest request);

    PaysResponse obtenirPaysParId(Long id);

    List<PaysResponse> obtenirTousLesPays();

    PaysResponse modifierPays(Long id, PaysRequest request);

    PageResponse<PaysResponse> obtenirTousLesPaysPageable(Pageable pageable);

    void supprimerPays(Long id);
}