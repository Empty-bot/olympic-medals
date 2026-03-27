package com.polytech.olympic_medals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.olympic_medals.dto.request.CompetitionRequest;
import com.polytech.olympic_medals.model.Competition;
import com.polytech.olympic_medals.model.StatutCompetition;
import com.polytech.olympic_medals.repository.CompetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Tests d'intégration du CompetitionController")
class CompetitionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompetitionRepository competitionRepository;

    private Competition competition;

    @BeforeEach
    void setUp() {
        competitionRepository.deleteAll();

        competition = competitionRepository.save(
                Competition.builder()
                        .nom("100m Hommes")
                        .discipline("Athlétisme")
                        .dateDebut(LocalDate.of(2026, 7, 15))
                        .dateFin(LocalDate.of(2026, 7, 15))
                        .statut(StatutCompetition.PLANIFIEE)
                        .build()
        );
    }

    // GET /api/v1/competitions

    @Test
    @DisplayName("GET /api/v1/competitions : retourne la liste des compétitions")
    void obtenirToutesLesCompetitions_retourneListe() throws Exception {
        mockMvc.perform(get("/api/v1/competitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].nom").value("100m Hommes"));
    }

    // GET /api/v1/competitions/{id}

    @Test
    @DisplayName("GET /api/v1/competitions/{id} : retourne la compétition")
    void obtenirCompetitionParId_succes() throws Exception {
        mockMvc.perform(get("/api/v1/competitions/{id}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nom").value("100m Hommes"))
                .andExpect(jsonPath("$.data.discipline").value("Athlétisme"))
                .andExpect(jsonPath("$.data.statut").value("PLANIFIEE"));
    }

    @Test
    @DisplayName("GET /api/v1/competitions/{id} : retourne 404 si ID inexistant")
    void obtenirCompetitionParId_idInexistant_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/competitions/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // POST /api/v1/competitions

    @Test
    @DisplayName("POST /api/v1/competitions : crée une compétition avec succès")
    void creerCompetition_succes() throws Exception {
        CompetitionRequest request = CompetitionRequest.builder()
                .nom("200m Femmes")
                .discipline("Athlétisme")
                .dateDebut(LocalDate.of(2026, 7, 16))
                .build();

        mockMvc.perform(post("/api/v1/competitions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nom").value("200m Femmes"))
                .andExpect(jsonPath("$.data.statut").value("PLANIFIEE"));
    }

    @Test
    @DisplayName("POST /api/v1/competitions : retourne 400 si données invalides")
    void creerCompetition_donneesInvalides_retourne400() throws Exception {
        CompetitionRequest request = CompetitionRequest.builder()
                .nom("")
                .discipline("")
                .build();

        mockMvc.perform(post("/api/v1/competitions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.nom").exists())
                .andExpect(jsonPath("$.data.discipline").exists());
    }

    // PUT /api/v1/competitions/{id}

    @Test
    @DisplayName("PUT /api/v1/competitions/{id} : modifie une compétition avec succès")
    void modifierCompetition_succes() throws Exception {
        CompetitionRequest request = CompetitionRequest.builder()
                .nom("100m Hommes Final")
                .discipline("Athlétisme")
                .dateDebut(LocalDate.of(2026, 7, 15))
                .statut(StatutCompetition.EN_COURS)
                .build();

        mockMvc.perform(put("/api/v1/competitions/{id}", competition.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nom").value("100m Hommes Final"))
                .andExpect(jsonPath("$.data.statut").value("EN_COURS"));
    }

    // DELETE /api/v1/competitions/{id}

    @Test
    @DisplayName("DELETE /api/v1/competitions/{id} : supprime une compétition")
    void supprimerCompetition_succes() throws Exception {
        mockMvc.perform(delete("/api/v1/competitions/{id}", competition.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/competitions/{id} : retourne 404 si ID inexistant")
    void supprimerCompetition_idInexistant_retourne404() throws Exception {
        mockMvc.perform(delete("/api/v1/competitions/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}