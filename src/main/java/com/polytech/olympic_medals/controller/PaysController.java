package com.polytech.olympic_medals.controller;

import com.polytech.olympic_medals.dto.request.PaysRequest;
import com.polytech.olympic_medals.dto.response.ApiResponse;
import com.polytech.olympic_medals.dto.response.PaysResponse;
import com.polytech.olympic_medals.service.PaysService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pays")
@RequiredArgsConstructor
@Slf4j
public class PaysController {

    private final PaysService paysService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaysResponse>> creerPays(
            @RequestBody @Valid PaysRequest request) {
        log.debug("POST /api/v1/pays");
        PaysResponse pays = paysService.creerPays(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(pays, "Pays créé avec succès"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaysResponse>>> obtenirTousLesPays() {
        log.debug("GET /api/v1/pays");
        List<PaysResponse> pays = paysService.obtenirTousLesPays();
        return ResponseEntity.ok(ApiResponse.success(pays, "Liste des pays récupérée"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaysResponse>> obtenirPaysParId(
            @PathVariable Long id) {
        log.debug("GET /api/v1/pays/{}", id);
        PaysResponse pays = paysService.obtenirPaysParId(id);
        return ResponseEntity.ok(ApiResponse.success(pays, "Pays récupéré avec succès"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaysResponse>> modifierPays(
            @PathVariable Long id,
            @RequestBody @Valid PaysRequest request) {
        log.debug("PUT /api/v1/pays/{}", id);
        PaysResponse pays = paysService.modifierPays(id, request);
        return ResponseEntity.ok(ApiResponse.success(pays, "Pays modifié avec succès"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerPays(@PathVariable Long id) {
        log.debug("DELETE /api/v1/pays/{}", id);
        paysService.supprimerPays(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Pays supprimé avec succès"));
    }
}