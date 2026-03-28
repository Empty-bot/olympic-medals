package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.CompetitionRequest;
import com.polytech.olympic_medals.dto.response.CompetitionResponse;
import com.polytech.olympic_medals.dto.response.PageResponse;
import com.polytech.olympic_medals.exception.ResourceNotFoundException;
import com.polytech.olympic_medals.model.Competition;
import com.polytech.olympic_medals.model.StatutCompetition;
import com.polytech.olympic_medals.repository.CompetitionRepository;
import com.polytech.olympic_medals.service.impl.CompetitionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du CompetitionService")
class CompetitionServiceImplTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private CompetitionServiceImpl competitionService;

    private Competition competition;
    private CompetitionRequest competitionRequest;

    @BeforeEach
    void setUp() {
        competition = Competition.builder()
                .id(1L)
                .nom("100m Hommes")
                .discipline("Athlétisme")
                .dateDebut(LocalDate.of(2026, 7, 15))
                .dateFin(LocalDate.of(2026, 7, 15))
                .statut(StatutCompetition.PLANIFIEE)
                .build();

        competitionRequest = CompetitionRequest.builder()
                .nom("100m Hommes")
                .discipline("Athlétisme")
                .dateDebut(LocalDate.of(2026, 7, 15))
                .dateFin(LocalDate.of(2026, 7, 15))
                .build();
    }

    // creerCompetition 

    @Test
    @DisplayName("creerCompetition : succès")
    void creerCompetition_succes() {
        // GIVEN
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        // WHEN
        CompetitionResponse resultat = competitionService.creerCompetition(competitionRequest);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("100m Hommes");
        assertThat(resultat.getStatut()).isEqualTo(StatutCompetition.PLANIFIEE);
        verify(competitionRepository, times(1)).save(any(Competition.class));
    }

    // obtenirCompetitionParId 

    @Test
    @DisplayName("obtenirCompetitionParId : succès")
    void obtenirCompetitionParId_succes() {
        // GIVEN
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // WHEN
        CompetitionResponse resultat = competitionService.obtenirCompetitionParId(1L);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getDiscipline()).isEqualTo("Athlétisme");
    }

    @Test
    @DisplayName("obtenirCompetitionParId : échec si ID inexistant")
    void obtenirCompetitionParId_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(competitionRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> competitionService.obtenirCompetitionParId(999L));
    }

    // obtenirToutesLesCompetitions 

    @Test
    @DisplayName("obtenirToutesLesCompetitions : retourne la liste complète")
    void obtenirToutesLesCompetitions_retourneListe() {
        // GIVEN
        when(competitionRepository.findAll()).thenReturn(List.of(competition));

        // WHEN
        List<CompetitionResponse> resultat = competitionService.obtenirToutesLesCompetitions();

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNom()).isEqualTo("100m Hommes");
    }

    // obtenirToutesLesCompetitionsPageable

    @Test
    @DisplayName("obtenirToutesLesCompetitionsPageable : retourne la page correcte")
    void obtenirToutesLesCompetitionsPageable_retournePage() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Competition> pageCompetition = new PageImpl<>(List.of(competition), pageable, 1);
        when(competitionRepository.findAll(pageable)).thenReturn(pageCompetition);

        // WHEN
        PageResponse<CompetitionResponse> resultat =
                competitionService.obtenirToutesLesCompetitionsPageable(pageable);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContenu()).hasSize(1);
        assertThat(resultat.getContenu().get(0).getNom()).isEqualTo("100m Hommes");
        assertThat(resultat.getTotalElements()).isEqualTo(1);
        assertThat(resultat.isPremiere()).isTrue();
    }

    // supprimerCompetition 

    @Test
    @DisplayName("supprimerCompetition : succès")
    void supprimerCompetition_succes() {
        // GIVEN
        when(competitionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(competitionRepository).deleteById(1L);

        // WHEN
        competitionService.supprimerCompetition(1L);

        // THEN
        verify(competitionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("supprimerCompetition : échec si ID inexistant")
    void supprimerCompetition_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(competitionRepository.existsById(999L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> competitionService.supprimerCompetition(999L));
        verify(competitionRepository, never()).deleteById(any());
    }

    // modifierCompetition

    @Test
    @DisplayName("modifierCompetition : succès")
    void modifierCompetition_succes() {
        // GIVEN
        CompetitionRequest request = CompetitionRequest.builder()
                .nom("100m Hommes Final")
                .discipline("Athlétisme")
                .dateDebut(LocalDate.of(2026, 7, 15))
                .statut(StatutCompetition.EN_COURS)
                .build();

        Competition competitionModifiee = Competition.builder()
                .id(1L).nom("100m Hommes Final")
                .discipline("Athlétisme")
                .dateDebut(LocalDate.of(2026, 7, 15))
                .statut(StatutCompetition.EN_COURS)
                .build();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competitionModifiee);

        // WHEN
        CompetitionResponse resultat = competitionService.modifierCompetition(1L, request);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("100m Hommes Final");
        assertThat(resultat.getStatut()).isEqualTo(StatutCompetition.EN_COURS);
        verify(competitionRepository, times(1)).save(any(Competition.class));
    }

    @Test
    @DisplayName("modifierCompetition : échec si ID inexistant")
    void modifierCompetition_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(competitionRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> competitionService.modifierCompetition(999L, competitionRequest));
    }
}