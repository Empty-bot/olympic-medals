package com.polytech.olympic_medals.controller;

import com.polytech.olympic_medals.dto.request.MedailleRequest;
import com.polytech.olympic_medals.dto.response.ApiResponse;
import com.polytech.olympic_medals.dto.response.MedailleResponse;
import com.polytech.olympic_medals.service.MedailleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medailles")
@RequiredArgsConstructor
@Slf4j
public class MedailleController {

    private final MedailleService medailleService;

    @PostMapping
    public ResponseEntity<ApiResponse<MedailleResponse>> enregistrerMedaille(
            @RequestBody @Valid MedailleRequest request) {
        log.debug("POST /api/v1/medailles");
        MedailleResponse medaille = medailleService.enregistrerMedaille(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(medaille, "Médaille enregistrée avec succès"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedailleResponse>>> obtenirToutesLesMedailles() {
        log.debug("GET /api/v1/medailles");
        List<MedailleResponse> medailles = medailleService.obtenirToutesLesMedailles();
        return ResponseEntity.ok(ApiResponse.success(medailles,
                "Liste des médailles récupérée"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedailleResponse>> obtenirMedailleParId(
            @PathVariable Long id) {
        log.debug("GET /api/v1/medailles/{}", id);
        MedailleResponse medaille = medailleService.obtenirMedailleParId(id);
        return ResponseEntity.ok(ApiResponse.success(medaille,
                "Médaille récupérée avec succès"));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<ApiResponse<List<MedailleResponse>>> obtenirMedaillesParAthlete(
            @PathVariable Long athleteId) {
        log.debug("GET /api/v1/medailles/athlete/{}", athleteId);
        List<MedailleResponse> medailles = medailleService.obtenirMedaillesParAthlete(athleteId);
        return ResponseEntity.ok(ApiResponse.success(medailles,
                "Médailles de l'athlète récupérées"));
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<ApiResponse<List<MedailleResponse>>> obtenirMedaillesParCompetition(
            @PathVariable Long competitionId) {
        log.debug("GET /api/v1/medailles/competition/{}", competitionId);
        List<MedailleResponse> medailles =
                medailleService.obtenirMedaillesParCompetition(competitionId);
        return ResponseEntity.ok(ApiResponse.success(medailles,
                "Médailles de la compétition récupérées"));
    }
}