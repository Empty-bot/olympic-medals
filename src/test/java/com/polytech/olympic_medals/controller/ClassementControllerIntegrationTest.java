package com.polytech.olympic_medals.controller;

import com.polytech.olympic_medals.model.*;
import com.polytech.olympic_medals.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Tests d'intégration du ClassementController")
class ClassementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MedailleRepository medailleRepository;

    private Pays pays;

    @BeforeEach
    void setUp() {
        medailleRepository.deleteAll();
        athleteRepository.deleteAll();
        competitionRepository.deleteAll();
        paysRepository.deleteAll();

        pays = paysRepository.save(
                Pays.builder().code("SEN").nom("Sénégal").drapeau("🇸🇳").build()
        );

        Athlete athlete = athleteRepository.save(
                Athlete.builder()
                        .nom("Diallo").prenom("Moussa")
                        .discipline("Sprint").pays(pays)
                        .build()
        );

        Competition competition = competitionRepository.save(
                Competition.builder()
                        .nom("100m Hommes").discipline("Athlétisme")
                        .dateDebut(LocalDate.of(2026, 7, 15))
                        .statut(StatutCompetition.TERMINEE)
                        .build()
        );

        medailleRepository.save(
                Medaille.builder()
                        .type(TypeMedaille.OR)
                        .dateObtention(LocalDate.of(2026, 7, 15))
                        .athlete(athlete).pays(pays).competition(competition)
                        .build()
        );
    }

    // GET /api/v1/classement

    @Test
    @DisplayName("GET /api/v1/classement : retourne le classement par total")
    void obtenirClassement_parTotal() throws Exception {
        mockMvc.perform(get("/api/v1/classement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].paysCode").value("SEN"))
                .andExpect(jsonPath("$.data[0].nbOr").value(1))
                .andExpect(jsonPath("$.data[0].total").value(1));
    }

    // GET /api/v1/classement?tri=or

    @Test
    @DisplayName("GET /api/v1/classement?tri=or : retourne le classement par or")
    void obtenirClassement_parOr() throws Exception {
        mockMvc.perform(get("/api/v1/classement").param("tri", "or"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nbOr").value(1));
    }

    // GET /api/v1/classement?tri=points

    @Test
    @DisplayName("GET /api/v1/classement?tri=points : retourne le classement par points")
    void obtenirClassement_parPoints() throws Exception {
        mockMvc.perform(get("/api/v1/classement").param("tri", "points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].points").value(3));
    }

    // GET /api/v1/classement/pays/{paysId}

    @Test
    @DisplayName("GET /api/v1/classement/pays/{paysId} : retourne les stats du pays")
    void obtenirStatsPays_succes() throws Exception {
        mockMvc.perform(get("/api/v1/classement/pays/{paysId}", pays.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paysCode").value("SEN"))
                .andExpect(jsonPath("$.data.nbOr").value(1))
                .andExpect(jsonPath("$.data.points").value(3));
    }

    @Test
    @DisplayName("GET /api/v1/classement/pays/{paysId} : retourne 404 si pays inexistant")
    void obtenirStatsPays_paysInexistant_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/classement/pays/{paysId}", 999L))
                .andExpect(status().isNotFound());
    }
}