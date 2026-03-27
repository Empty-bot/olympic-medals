package com.polytech.olympic_medals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.olympic_medals.dto.request.MedailleRequest;
import com.polytech.olympic_medals.model.*;
import com.polytech.olympic_medals.repository.*;
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
@DisplayName("Tests d'intégration du MedailleController")
class MedailleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MedailleRepository medailleRepository;

    private Pays pays;
    private Athlete athlete;
    private Competition competition;
    private Medaille medaille;

    @BeforeEach
    void setUp() {
        medailleRepository.deleteAll();
        athleteRepository.deleteAll();
        competitionRepository.deleteAll();
        paysRepository.deleteAll();

        pays = paysRepository.save(
                Pays.builder().code("SEN").nom("Sénégal").drapeau("🇸🇳").build()
        );

        athlete = athleteRepository.save(
                Athlete.builder()
                        .nom("Diallo").prenom("Moussa")
                        .discipline("Sprint").pays(pays)
                        .build()
        );

        competition = competitionRepository.save(
                Competition.builder()
                        .nom("100m Hommes").discipline("Athlétisme")
                        .dateDebut(LocalDate.of(2026, 7, 15))
                        .statut(StatutCompetition.EN_COURS)
                        .build()
        );

        medaille = medailleRepository.save(
                Medaille.builder()
                        .type(TypeMedaille.OR)
                        .dateObtention(LocalDate.of(2026, 7, 15))
                        .athlete(athlete).pays(pays).competition(competition)
                        .build()
        );
    }

    // GET /api/v1/medailles 

    @Test
    @DisplayName("GET /api/v1/medailles : retourne la liste des médailles")
    void obtenirToutesLesMedailles_retourneListe() throws Exception {
        mockMvc.perform(get("/api/v1/medailles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].type").value("OR"));
    }

    // GET /api/v1/medailles/{id}

    @Test
    @DisplayName("GET /api/v1/medailles/{id} : retourne la médaille")
    void obtenirMedailleParId_succes() throws Exception {
        mockMvc.perform(get("/api/v1/medailles/{id}", medaille.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.type").value("OR"))
                .andExpect(jsonPath("$.data.pays.code").value("SEN"))
                .andExpect(jsonPath("$.data.athlete.nom").value("Diallo"));
    }

    @Test
    @DisplayName("GET /api/v1/medailles/{id} : retourne 404 si ID inexistant")
    void obtenirMedailleParId_idInexistant_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/medailles/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // GET /api/v1/medalles/athlete/{athleteId}

    @Test
    @DisplayName("GET /api/v1/medailles/athlete/{athleteId} : retourne les médailles de l'athlète")
    void obtenirMedaillesParAthlete_succes() throws Exception {
        mockMvc.perform(get("/api/v1/medailles/athlete/{athleteId}", athlete.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].type").value("OR"));
    }

    @Test
    @DisplayName("GET /api/v1/medailles/athlete/{athleteId} : retourne 404 si athlète inexistant")
    void obtenirMedaillesParAthlete_athleteInexistant_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/medailles/athlete/{athleteId}", 999L))
                .andExpect(status().isNotFound());
    }

    // GET /api/v1/medalles/competition/{competitionId}

    @Test
    @DisplayName("GET /api/v1/medailles/competition/{competitionId} : retourne les médailles d'une compétition")
    void obtenirMedaillesParCompetition_succes() throws Exception {
        mockMvc.perform(get("/api/v1/medailles/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].type").value("OR"));
    }

    @Test
    @DisplayName("GET /api/v1/medailles/competition/{competitionId} : retourne 404 si compétition inexistante")
    void obtenirMedaillesParCompetition_competitionInexistante_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/medailles/competition/{competitionId}", 999L))
                .andExpect(status().isNotFound());
    }

    // POST /api/v1/medailles

    @Test
    @DisplayName("POST /api/v1/medailles : enregistre une médaille avec succès")
    void enregistrerMedaille_succes() throws Exception {
        MedailleRequest request = MedailleRequest.builder()
                .type(TypeMedaille.ARGENT)
                .dateObtention(LocalDate.of(2026, 7, 15))
                .athleteId(athlete.getId())
                .paysId(pays.getId())
                .competitionId(competition.getId())
                .build();

        mockMvc.perform(post("/api/v1/medailles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.type").value("ARGENT"))
                .andExpect(jsonPath("$.data.athlete.nom").value("Diallo"));
    }

    @Test
    @DisplayName("POST /api/v1/medailles : retourne 400 si données invalides")
    void enregistrerMedaille_donneesInvalides_retourne400() throws Exception {
        MedailleRequest request = MedailleRequest.builder()
                .type(null)
                .athleteId(null)
                .paysId(null)
                .competitionId(null)
                .build();

        mockMvc.perform(post("/api/v1/medailles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/medailles : retourne 404 si athlète inexistant")
    void enregistrerMedaille_athleteInexistant_retourne404() throws Exception {
        MedailleRequest request = MedailleRequest.builder()
                .type(TypeMedaille.BRONZE)
                .athleteId(999L)
                .paysId(pays.getId())
                .competitionId(competition.getId())
                .build();

        mockMvc.perform(post("/api/v1/medailles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}