package com.polytech.olympic_medals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.olympic_medals.dto.request.AthleteRequest;
import com.polytech.olympic_medals.model.Athlete;
import com.polytech.olympic_medals.model.Pays;
import com.polytech.olympic_medals.repository.AthleteRepository;
import com.polytech.olympic_medals.repository.PaysRepository;
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
@DisplayName("Tests d'intégration du AthleteController")
class AthleteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private AthleteRepository athleteRepository;

    private Pays pays;
    private Athlete athlete;

    @BeforeEach
    void setUp() {
        athleteRepository.deleteAll();
        paysRepository.deleteAll();

        pays = paysRepository.save(
                Pays.builder().code("SEN").nom("Sénégal").drapeau("🇸🇳").build()
        );

        athlete = athleteRepository.save(
                Athlete.builder()
                        .nom("Diallo").prenom("Diouldé")
                        .dateNaissance(LocalDate.of(1995, 3, 15))
                        .discipline("Sprint").pays(pays)
                        .build()
        );
    }

    // GET /api/v1/athletes

    @Test
    @DisplayName("GET /api/v1/athletes : retourne la liste des athlètes")
    void obtenirTousLesAthletes_retourneListe() throws Exception {
        mockMvc.perform(get("/api/v1/athletes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].nom").value("Diallo"));
    }

    // GET /api/v1/athletes/pageable

    @Test
    @DisplayName("GET /api/v1/athletes/pageable : retourne la liste paginée")
    void obtenirTousLesAthletesPageable_retournePage() throws Exception {
        mockMvc.perform(get("/api/v1/athletes/pageable")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.contenu", hasSize(1)))
                .andExpect(jsonPath("$.data.contenu[0].nom").value("Diallo"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.premiere").value(true));
    }

    // GET /api/v1/athletes/{id}

    @Test
    @DisplayName("GET /api/v1/athletes/{id} : retourne l'athlète")
    void obtenirAthleteParId_succes() throws Exception {
        mockMvc.perform(get("/api/v1/athletes/{id}", athlete.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nom").value("Diallo"))
                .andExpect(jsonPath("$.data.pays.code").value("SEN"));
    }

    @Test
    @DisplayName("GET /api/v1/athletes/{id} : retourne 404 si ID inexistant")
    void obtenirAthleteParId_idInexistant_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/athletes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    // GET /api/v1/athletes/pays/{paysId}

    @Test
    @DisplayName("GET /api/v1/athletes/pays/{paysId} : retourne les athlètes du pays")
    void obtenirAthleteParPays_succes() throws Exception {
        mockMvc.perform(get("/api/v1/athletes/pays/{paysId}", pays.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].prenom").value("Diouldé"));
    }

    @Test
    @DisplayName("GET /api/v1/athletes/pays/{paysId} : retourne 404 si ID inexistant")
    void obtenirAthleteParPays_paysIdInexistant_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/athletes/pays/{paysId}", 999L))
                .andExpect(status().isNotFound());
    }

    // POST /api/v1/athletes

    @Test
    @DisplayName("POST /api/v1/athletes : crée un athlète avec succès")
    void creerAthlete_succes() throws Exception {
        AthleteRequest request = AthleteRequest.builder()
                .nom("Faye").prenom("Mada")
                .dateNaissance(LocalDate.of(1998, 6, 20))
                .discipline("Natation")
                .paysId(pays.getId())
                .build();

        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nom").value("Faye"))
                .andExpect(jsonPath("$.data.pays.code").value("SEN"));
    }

    @Test
    @DisplayName("POST /api/v1/athletes : retourne 400 si données invalides")
    void creerAthlete_donneesInvalides_retourne400() throws Exception {
        AthleteRequest request = AthleteRequest.builder()
                .nom("").prenom("")
                .paysId(pays.getId())
                .build();

        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // PUT /api/v1/athletes/{id}

    @Test
    @DisplayName("PUT /api/v1/athletes/{id} : modifie un athlète avec succès")
    void modifierAthlete_succes() throws Exception {
        AthleteRequest request = AthleteRequest.builder()
                .nom("Diallo").prenom("Diouldé")
                .dateNaissance(LocalDate.of(1995, 3, 15))
                .discipline("Natation")
                .paysId(pays.getId())
                .build();

        mockMvc.perform(put("/api/v1/athletes/{id}", athlete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.discipline").value("Natation"));
    }

    // DELETE /api/v1/athletes/{id}

    @Test
    @DisplayName("DELETE /api/v1/athletes/{id} : supprime un athlète")
    void supprimerAthlete_succes() throws Exception {
        mockMvc.perform(delete("/api/v1/athletes/{id}", athlete.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/athletes/{id} : retourne 404 si ID inexistant")
    void supprimerAthlete_idInexistant_retourne404() throws Exception {
        mockMvc.perform(delete("/api/v1/athletes/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}