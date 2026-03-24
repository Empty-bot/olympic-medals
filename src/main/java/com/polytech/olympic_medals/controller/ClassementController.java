package com.polytech.olympic_medals.controller;

import com.polytech.olympic_medals.dto.response.ApiResponse;
import com.polytech.olympic_medals.dto.response.ClassementResponse;
import com.polytech.olympic_medals.service.MedailleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classement")
@RequiredArgsConstructor
@Slf4j
public class ClassementController {

    private final MedailleService medailleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassementResponse>>> obtenirClassement(
            @RequestParam(defaultValue = "total") String tri) {
        log.debug("GET /api/v1/classement?tri={}", tri);
        List<ClassementResponse> classement = medailleService.obtenirClassement(tri);
        return ResponseEntity.ok(ApiResponse.success(classement,
                "Classement récupéré avec succès"));
    }

    @GetMapping("/pays/{paysId}")
    public ResponseEntity<ApiResponse<ClassementResponse>> obtenirStatsPays(
            @PathVariable Long paysId) {
        log.debug("GET /api/v1/classement/pays/{}", paysId);
        ClassementResponse stats = medailleService.obtenirStatsPays(paysId);
        return ResponseEntity.ok(ApiResponse.success(stats,
                "Statistiques du pays récupérées"));
    }
}