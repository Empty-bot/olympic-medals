package com.polytech.olympic_medals.controller;

import com.polytech.olympic_medals.dto.request.CompetitionRequest;
import com.polytech.olympic_medals.dto.response.ApiResponse;
import com.polytech.olympic_medals.dto.response.CompetitionResponse;
import com.polytech.olympic_medals.dto.response.PageResponse;
import com.polytech.olympic_medals.service.CompetitionService;
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
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
@Slf4j
public class CompetitionController {

    private final CompetitionService competitionService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompetitionResponse>> creerCompetition(
            @RequestBody @Valid CompetitionRequest request) {
        log.debug("POST /api/v1/competitions");
        CompetitionResponse competition = competitionService.creerCompetition(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(competition, "Compétition créée avec succès"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompetitionResponse>>> obtenirToutesLesCompetitions() {
        log.debug("GET /api/v1/competitions");
        List<CompetitionResponse> competitions = competitionService.obtenirToutesLesCompetitions();
        return ResponseEntity.ok(ApiResponse.success(competitions,
                "Liste des compétitions récupérée"));
    }

    @GetMapping("/pageable")
    public ResponseEntity<ApiResponse<PageResponse<CompetitionResponse>>> obtenirToutesLesCompetitionsPageable(
            @PageableDefault(size = 10, sort = "nom") Pageable pageable) {
        log.debug("GET /api/v1/competitions/pageable");
        PageResponse<CompetitionResponse> page =
                competitionService.obtenirToutesLesCompetitionsPageable(pageable);
        return ResponseEntity.ok(ApiResponse.success(page,
                "Liste paginée des compétitions récupérée"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompetitionResponse>> obtenirCompetitionParId(
            @PathVariable Long id) {
        log.debug("GET /api/v1/competitions/{}", id);
        CompetitionResponse competition = competitionService.obtenirCompetitionParId(id);
        return ResponseEntity.ok(ApiResponse.success(competition,
                "Compétition récupérée avec succès"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompetitionResponse>> modifierCompetition(
            @PathVariable Long id,
            @RequestBody @Valid CompetitionRequest request) {
        log.debug("PUT /api/v1/competitions/{}", id);
        CompetitionResponse competition = competitionService.modifierCompetition(id, request);
        return ResponseEntity.ok(ApiResponse.success(competition,
                "Compétition modifiée avec succès"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerCompetition(@PathVariable Long id) {
        log.debug("DELETE /api/v1/competitions/{}", id);
        competitionService.supprimerCompetition(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Compétition supprimée avec succès"));
    }
}