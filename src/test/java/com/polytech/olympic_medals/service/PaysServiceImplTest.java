package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.PaysRequest;
import com.polytech.olympic_medals.dto.response.PaysResponse;
import com.polytech.olympic_medals.exception.DuplicateResourceException;
import com.polytech.olympic_medals.exception.ResourceNotFoundException;
import com.polytech.olympic_medals.model.Pays;
import com.polytech.olympic_medals.repository.PaysRepository;
import com.polytech.olympic_medals.service.impl.PaysServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du PaysService")
class PaysServiceImplTest {

    @Mock
    private PaysRepository paysRepository;

    @InjectMocks
    private PaysServiceImpl paysService;

    private Pays pays;
    private PaysRequest paysRequest;

    @BeforeEach
    void setUp() {
        pays = Pays.builder()
                .id(1L)
                .code("SEN")
                .nom("Sénégal")
                .drapeau("🇸🇳")
                .build();

        paysRequest = PaysRequest.builder()
                .code("SEN")
                .nom("Sénégal")
                .drapeau("🇸🇳")
                .build();
    }

    // creerPays

    @Test
    @DisplayName("creerPays : succès")
    void creerPays_succes() {
        // GIVEN
        when(paysRepository.existsByCode("SEN")).thenReturn(false);
        when(paysRepository.save(any(Pays.class))).thenReturn(pays);

        // WHEN
        PaysResponse resultat = paysService.creerPays(paysRequest);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getCode()).isEqualTo("SEN");
        assertThat(resultat.getNom()).isEqualTo("Sénégal");
        verify(paysRepository, times(1)).save(any(Pays.class));
    }

    @Test
    @DisplayName("creerPays : échec si code déjà existant")
    void creerPays_codeDuplique_lanceDuplicateResourceException() {
        // GIVEN
        when(paysRepository.existsByCode("SEN")).thenReturn(true);

        // WHEN + THEN
        assertThrows(DuplicateResourceException.class,
                () -> paysService.creerPays(paysRequest));
        verify(paysRepository, never()).save(any(Pays.class));
    }

    // obtenirPaysParId

    @Test
    @DisplayName("obtenirPaysParId : succès")
    void obtenirPaysParId_succes() {
        // GIVEN
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));

        // WHEN
        PaysResponse resultat = paysService.obtenirPaysParId(1L);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getNom()).isEqualTo("Sénégal");
    }

    @Test
    @DisplayName("obtenirPaysParId : échec si ID inexistant")
    void obtenirPaysParId_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(paysRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> paysService.obtenirPaysParId(999L));
    }

    // obtenirTousLesPays 

    @Test
    @DisplayName("obtenirTousLesPays : retourne la liste complète")
    void obtenirTousLesPays_retourneListe() {
        // GIVEN
        when(paysRepository.findAll()).thenReturn(List.of(pays));

        // WHEN
        List<PaysResponse> resultat = paysService.obtenirTousLesPays();

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCode()).isEqualTo("SEN");
    }

    // supprimerPays

    @Test
    @DisplayName("supprimerPays : succès")
    void supprimerPays_succes() {
        // GIVEN
        when(paysRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paysRepository).deleteById(1L);

        // WHEN
        paysService.supprimerPays(1L);

        // THEN
        verify(paysRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("supprimerPays : échec si ID inexistant")
    void supprimerPays_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(paysRepository.existsById(999L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> paysService.supprimerPays(999L));
        verify(paysRepository, never()).deleteById(any());
    }

    // modifierPays

    @Test
    @DisplayName("modifierPays : succès")
    void modifierPays_succes() {
        // GIVEN
        PaysRequest request = PaysRequest.builder()
                .code("SEN")
                .nom("République du Sénégal")
                .drapeau("🇸🇳")
                .build();

        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(paysRepository.save(any(Pays.class))).thenReturn(
                Pays.builder().id(1L).code("SEN").nom("République du Sénégal").drapeau("🇸🇳").build()
        );

        // WHEN
        PaysResponse resultat = paysService.modifierPays(1L, request);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("République du Sénégal");
        verify(paysRepository, times(1)).save(any(Pays.class));
    }

    @Test
    @DisplayName("modifierPays : échec si ID inexistant")
    void modifierPays_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(paysRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> paysService.modifierPays(999L, paysRequest));
    }

    @Test
    @DisplayName("modifierPays : échec si nouveau code déjà pris par un autre pays")
    void modifierPays_codeDuplique_lanceDuplicateResourceException() {
        // GIVEN
        PaysRequest request = PaysRequest.builder()
                .code("FRA").nom("Sénégal").build();

        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(paysRepository.existsByCode("FRA")).thenReturn(true);

        // WHEN + THEN
        assertThrows(DuplicateResourceException.class,
                () -> paysService.modifierPays(1L, request));
    }
}