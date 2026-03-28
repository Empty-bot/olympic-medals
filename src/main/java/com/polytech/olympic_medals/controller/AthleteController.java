package com.polytech.olympic_medals.controller;

import com.polytech.olympic_medals.dto.request.AthleteRequest;
import com.polytech.olympic_medals.dto.response.ApiResponse;
import com.polytech.olympic_medals.dto.response.AthleteResponse;
import com.polytech.olympic_medals.dto.response.PageResponse;
import com.polytech.olympic_medals.service.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;


import java.util.List;

@RestController
@RequestMapping("/api/v1/athletes")
@RequiredArgsConstructor
@Slf4j
public class AthleteController {

    private final AthleteService athleteService;

    @PostMapping
    public ResponseEntity<ApiResponse<AthleteResponse>> creerAthlete(
            @RequestBody @Valid AthleteRequest request) {
        log.debug("POST /api/v1/athletes");
        AthleteResponse athlete = athleteService.creerAthlete(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(athlete, "Athlète créé avec succès"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AthleteResponse>>> obtenirTousLesAthletes() {
        log.debug("GET /api/v1/athletes");
        List<AthleteResponse> athletes = athleteService.obtenirTousLesAthletes();
        return ResponseEntity.ok(ApiResponse.success(athletes, "Liste des athlètes récupérée"));
    }

    @GetMapping("/pageable")
    public ResponseEntity<ApiResponse<PageResponse<AthleteResponse>>> obtenirTousLesAthletesPageable(
            @PageableDefault(size = 10, sort = "nom") Pageable pageable) {
        log.debug("GET /api/v1/athletes/pageable");
        PageResponse<AthleteResponse> page = athleteService.obtenirTousLesAthletesPageable(pageable);
        return ResponseEntity.ok(ApiResponse.success(page, "Liste paginée des athlètes récupérée"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AthleteResponse>> obtenirAthleteParId(
            @PathVariable Long id) {
        log.debug("GET /api/v1/athletes/{}", id);
        AthleteResponse athlete = athleteService.obtenirAthleteParId(id);
        return ResponseEntity.ok(ApiResponse.success(athlete, "Athlète récupéré avec succès"));
    }

    @GetMapping("/pays/{paysId}")
    public ResponseEntity<ApiResponse<List<AthleteResponse>>> obtenirAthleteParPays(
            @PathVariable Long paysId) {
        log.debug("GET /api/v1/athletes/pays/{}", paysId);
        List<AthleteResponse> athletes = athleteService.obtenirAthleteParPays(paysId);
        return ResponseEntity.ok(ApiResponse.success(athletes,
                "Athlètes du pays récupérés avec succès"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AthleteResponse>> modifierAthlete(
            @PathVariable Long id,
            @RequestBody @Valid AthleteRequest request) {
        log.debug("PUT /api/v1/athletes/{}", id);
        AthleteResponse athlete = athleteService.modifierAthlete(id, request);
        return ResponseEntity.ok(ApiResponse.success(athlete, "Athlète modifié avec succès"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerAthlete(@PathVariable Long id) {
        log.debug("DELETE /api/v1/athletes/{}", id);
        athleteService.supprimerAthlete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Athlète supprimé avec succès"));
    }
}