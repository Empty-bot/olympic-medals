package com.polytech.olympic_medals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polytech.olympic_medals.dto.request.PaysRequest;
import com.polytech.olympic_medals.model.Pays;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Tests d'intégration du PaysController")
class PaysControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaysRepository paysRepository;

    private Pays paysSauvegarde;

    @BeforeEach
    void setUp() {
        paysRepository.deleteAll();
        paysSauvegarde = paysRepository.save(
                Pays.builder()
                        .code("SEN")
                        .nom("Sénégal")
                        .drapeau("🇸🇳")
                        .build()
        );
    }

    // GET /api/v1/pays 

    @Test
    @DisplayName("GET /api/v1/pays : retourne la liste des pays")
    void obtenirTousLesPays_retourneListe() throws Exception {
        mockMvc.perform(get("/api/v1/pays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].code").value("SEN"));
    }

    // GET /api/v1/pays/{id} 

    @Test
    @DisplayName("GET /api/v1/pays/{id} : retourne le pays")
    void obtenirPaysParId_succes() throws Exception {
        mockMvc.perform(get("/api/v1/pays/{id}", paysSauvegarde.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("SEN"))
                .andExpect(jsonPath("$.data.nom").value("Sénégal"));
    }

    @Test
    @DisplayName("GET /api/v1/pays/{id} : retourne 404 si ID inexistant")
    void obtenirPaysParId_idInexistant_retourne404() throws Exception {
        mockMvc.perform(get("/api/v1/pays/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // POST /api/v1/pays 

    @Test
    @DisplayName("POST /api/v1/pays : crée un pays avec succès")
    void creerPays_succes() throws Exception {
        PaysRequest request = PaysRequest.builder()
                .code("FRA")
                .nom("France")
                .drapeau("🇫🇷")
                .build();

        mockMvc.perform(post("/api/v1/pays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("FRA"))
                .andExpect(jsonPath("$.data.nom").value("France"));
    }

    @Test
    @DisplayName("POST /api/v1/pays : retourne 400 si données invalides")
    void creerPays_donneesInvalides_retourne400() throws Exception {
        PaysRequest request = PaysRequest.builder()
                .code("")
                .nom("")
                .build();

        mockMvc.perform(post("/api/v1/pays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.code").exists())
                .andExpect(jsonPath("$.data.nom").exists());
    }

    @Test
    @DisplayName("POST /api/v1/pays : retourne 409 si code déjà existant")
    void creerPays_codeDuplique_retourne409() throws Exception {
        PaysRequest request = PaysRequest.builder()
                .code("SEN")
                .nom("Sénégal bis")
                .build();

        mockMvc.perform(post("/api/v1/pays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // PUT /api/v1/pays/{id} 

    @Test
    @DisplayName("PUT /api/v1/pays/{id} : modifie un pays avec succès")
    void modifierPays_succes() throws Exception {
        PaysRequest request = PaysRequest.builder()
                .code("SEN")
                .nom("République du Sénégal")
                .drapeau("🇸🇳")
                .build();

        mockMvc.perform(put("/api/v1/pays/{id}", paysSauvegarde.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nom").value("République du Sénégal"));
    }

    // DELETE /api/v1/pays/{id} 

    @Test
    @DisplayName("DELETE /api/v1/pays/{id} : supprime un pays avec succès")
    void supprimerPays_succes() throws Exception {
        mockMvc.perform(delete("/api/v1/pays/{id}", paysSauvegarde.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/pays/{id} : retourne 404 si ID inexistant")
    void supprimerPays_idInexistant_retourne404() throws Exception {
        mockMvc.perform(delete("/api/v1/pays/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}