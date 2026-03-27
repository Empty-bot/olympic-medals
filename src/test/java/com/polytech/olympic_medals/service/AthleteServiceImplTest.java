package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.AthleteRequest;
import com.polytech.olympic_medals.dto.response.AthleteResponse;
import com.polytech.olympic_medals.exception.DuplicateResourceException;
import com.polytech.olympic_medals.exception.ResourceNotFoundException;
import com.polytech.olympic_medals.model.Athlete;
import com.polytech.olympic_medals.model.Pays;
import com.polytech.olympic_medals.repository.AthleteRepository;
import com.polytech.olympic_medals.repository.PaysRepository;
import com.polytech.olympic_medals.service.impl.AthleteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du AthleteService")
class AthleteServiceImplTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private PaysRepository paysRepository;

    @InjectMocks
    private AthleteServiceImpl athleteService;

    private Pays pays;
    private Athlete athlete;
    private AthleteRequest athleteRequest;

    @BeforeEach
    void setUp() {
        pays = Pays.builder()
                .id(1L)
                .code("SEN")
                .nom("Sénégal")
                .drapeau("🇸🇳")
                .build();

        athlete = Athlete.builder()
                .id(1L)
                .nom("Dia")
                .prenom("Maguette")
                .dateNaissance(LocalDate.of(1995, 3, 15))
                .discipline("Sprint")
                .pays(pays)
                .build();

        athleteRequest = AthleteRequest.builder()
                .nom("Dia")
                .prenom("Maguette")
                .dateNaissance(LocalDate.of(1995, 3, 15))
                .discipline("Sprint")
                .paysId(1L)
                .build();
    }

    // creerAthlete

    @Test
    @DisplayName("creerAthlete : succès")
    void creerAthlete_succes() {
        // GIVEN
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(athleteRepository.existsByNomAndPrenomAndPaysId("Dia", "Maguette", 1L))
                .thenReturn(false);
        when(athleteRepository.save(any(Athlete.class))).thenReturn(athlete);

        // WHEN
        AthleteResponse resultat = athleteService.creerAthlete(athleteRequest);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Dia");
        assertThat(resultat.getPrenom()).isEqualTo("Maguette");
        assertThat(resultat.getPays().getCode()).isEqualTo("SEN");
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("creerAthlete : échec si pays inexistant")
    void creerAthlete_paysInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> athleteService.creerAthlete(athleteRequest));
        verify(athleteRepository, never()).save(any());
    }

    @Test
    @DisplayName("creerAthlete : échec si athlète déjà existant")
    void creerAthlete_athleteDuplique_lanceDuplicateResourceException() {
        // GIVEN
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(athleteRepository.existsByNomAndPrenomAndPaysId("Dia", "Maguette", 1L))
                .thenReturn(true);

        // WHEN + THEN
        assertThrows(DuplicateResourceException.class,
                () -> athleteService.creerAthlete(athleteRequest));
        verify(athleteRepository, never()).save(any());
    }

    // obtenirAthleteParId 

    @Test
    @DisplayName("obtenirAthleteParId : succès")
    void obtenirAthleteParId_succes() {
        // GIVEN
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));

        // WHEN
        AthleteResponse resultat = athleteService.obtenirAthleteParId(1L);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getDiscipline()).isEqualTo("Sprint");
    }

    @Test
    @DisplayName("obtenirAthleteParId : échec si ID inexistant")
    void obtenirAthleteParId_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> athleteService.obtenirAthleteParId(999L));
    }

    // obtenirAthleteParPays 

    @Test
    @DisplayName("obtenirAthleteParPays : succès")
    void obtenirAthleteParPays_succes() {
        // GIVEN
        when(paysRepository.existsById(1L)).thenReturn(true);
        when(athleteRepository.findByPaysId(1L)).thenReturn(List.of(athlete));

        // WHEN
        List<AthleteResponse> resultat = athleteService.obtenirAthleteParPays(1L);

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNom()).isEqualTo("Dia");
    }

    @Test
    @DisplayName("obtenirAthleteParPays : échec si pays inexistant")
    void obtenirAthleteParPays_paysInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(paysRepository.existsById(999L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> athleteService.obtenirAthleteParPays(999L));
    }

    // supprimerAthlete 

    @Test
    @DisplayName("supprimerAthlete : succès")
    void supprimerAthlete_succes() {
        // GIVEN
        when(athleteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(athleteRepository).deleteById(1L);

        // WHEN
        athleteService.supprimerAthlete(1L);

        // THEN
        verify(athleteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("supprimerAthlete : échec si ID inexistant")
    void supprimerAthlete_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(athleteRepository.existsById(999L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> athleteService.supprimerAthlete(999L));
        verify(athleteRepository, never()).deleteById(any());
    }

    // modifierAthlete

    @Test
    @DisplayName("modifierAthlete : succès")
    void modifierAthlete_succes() {
        // GIVEN
        AthleteRequest request = AthleteRequest.builder()
                .nom("Diallo").prenom("Moussa")
                .dateNaissance(LocalDate.of(1995, 3, 15))
                .discipline("Natation")
                .paysId(1L)
                .build();

        Athlete athleteModifie = Athlete.builder()
                .id(1L).nom("Diallo").prenom("Moussa")
                .discipline("Natation").pays(pays)
                .build();

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(athleteModifie);

        // WHEN
        AthleteResponse resultat = athleteService.modifierAthlete(1L, request);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getDiscipline()).isEqualTo("Natation");
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("modifierAthlete : échec si ID inexistant")
    void modifierAthlete_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> athleteService.modifierAthlete(999L, athleteRequest));
    }
}