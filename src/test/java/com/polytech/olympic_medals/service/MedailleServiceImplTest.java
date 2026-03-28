package com.polytech.olympic_medals.service;

import com.polytech.olympic_medals.dto.request.MedailleRequest;
import com.polytech.olympic_medals.dto.response.ClassementResponse;
import com.polytech.olympic_medals.dto.response.MedailleResponse;
import com.polytech.olympic_medals.dto.response.PageResponse;
import com.polytech.olympic_medals.exception.ResourceNotFoundException;
import com.polytech.olympic_medals.model.*;
import com.polytech.olympic_medals.repository.*;
import com.polytech.olympic_medals.service.impl.MedailleServiceImpl;
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
@DisplayName("Tests du MedailleService")
class MedailleServiceImplTest {

    @Mock
    private MedailleRepository medailleRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private PaysRepository paysRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private MedailleServiceImpl medailleService;

    private Pays pays;
    private Athlete athlete;
    private Competition competition;
    private Medaille medaille;
    private MedailleRequest medailleRequest;

    @BeforeEach
    void setUp() {
        pays = Pays.builder()
                .id(1L).code("SEN").nom("Sénégal").drapeau("🇸🇳")
                .build();

        athlete = Athlete.builder()
                .id(1L).nom("Diallo").prenom("Moussa")
                .discipline("Sprint").pays(pays)
                .build();

        competition = Competition.builder()
                .id(1L).nom("100m Hommes").discipline("Athlétisme")
                .dateDebut(LocalDate.of(2026, 7, 15))
                .statut(StatutCompetition.EN_COURS)
                .build();

        medaille = Medaille.builder()
                .id(1L).type(TypeMedaille.OR)
                .dateObtention(LocalDate.of(2026, 7, 15))
                .athlete(athlete).pays(pays).competition(competition)
                .build();

        medailleRequest = MedailleRequest.builder()
                .type(TypeMedaille.OR)
                .dateObtention(LocalDate.of(2026, 7, 15))
                .athleteId(1L).paysId(1L).competitionId(1L)
                .build();
    }

    // enregistrerMedaille

    @Test
    @DisplayName("enregistrerMedaille : succès")
    void enregistrerMedaille_succes() {
        // GIVEN
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(medailleRepository.save(any(Medaille.class))).thenReturn(medaille);

        // WHEN
        MedailleResponse resultat = medailleService.enregistrerMedaille(medailleRequest);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getType()).isEqualTo(TypeMedaille.OR);
        assertThat(resultat.getPays().getCode()).isEqualTo("SEN");
        verify(medailleRepository, times(1)).save(any(Medaille.class));
    }

    @Test
    @DisplayName("enregistrerMedaille : échec si athlète inexistant")
    void enregistrerMedaille_athleteInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> medailleService.enregistrerMedaille(medailleRequest));
        verify(medailleRepository, never()).save(any());
    }

    @Test
    @DisplayName("enregistrerMedaille : échec si pays inexistant")
    void enregistrerMedaille_paysInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(paysRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> medailleService.enregistrerMedaille(medailleRequest));
        verify(medailleRepository, never()).save(any());
    }

    // obtenirMedailleParId

    @Test
    @DisplayName("obtenirMedailleParId : succès")
    void obtenirMedailleParId_succes() {
        // GIVEN
        when(medailleRepository.findById(1L)).thenReturn(Optional.of(medaille));

        // WHEN
        MedailleResponse resultat = medailleService.obtenirMedailleParId(1L);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getType()).isEqualTo(TypeMedaille.OR);
    }

    @Test
    @DisplayName("obtenirMedailleParId : échec si ID inexistant")
    void obtenirMedailleParId_idInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(medailleRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> medailleService.obtenirMedailleParId(999L));
    }

    // obtenirMedaillesParAthlete 

    @Test
    @DisplayName("obtenirMedaillesParAthlete : succès")
    void obtenirMedaillesParAthlete_succes() {
        // GIVEN
        when(athleteRepository.existsById(1L)).thenReturn(true);
        when(medailleRepository.findByAthleteId(1L)).thenReturn(List.of(medaille));

        // WHEN
        List<MedailleResponse> resultat = medailleService.obtenirMedaillesParAthlete(1L);

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getType()).isEqualTo(TypeMedaille.OR);
    }

    @Test
    @DisplayName("obtenirMedaillesParAthlete : échec si athlète inexistant")
    void obtenirMedaillesParAthlete_athleteInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(athleteRepository.existsById(999L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> medailleService.obtenirMedaillesParAthlete(999L));
    }

    // obtenirToutesLesMedailles

    @Test
    @DisplayName("obtenirToutesLesMedailles : succès")
    void obtenirToutesLesMedailles_succes() {
        // GIVEN
        when(medailleRepository.findAll()).thenReturn(List.of(medaille));

        // WHEN 
        List<MedailleResponse> resultat = medailleService.obtenirToutesLesMedailles();

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getType()).isEqualTo(TypeMedaille.OR);
    }

    // obtenirToutesLesMedaillesPageable

    @Test
    @DisplayName("obtenirToutesLesMedaillesPageable : retourne la page correcte")
    void obtenirToutesLesMedaillesPageable_retournePage() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Medaille> pageMedaille = new PageImpl<>(List.of(medaille), pageable, 1);
        when(medailleRepository.findAll(pageable)).thenReturn(pageMedaille);

        // WHEN
        PageResponse<MedailleResponse> resultat =
                medailleService.obtenirToutesLesMedaillesPageable(pageable);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContenu()).hasSize(1);
        assertThat(resultat.getContenu().get(0).getType()).isEqualTo(TypeMedaille.OR);
        assertThat(resultat.getTotalElements()).isEqualTo(1);
        assertThat(resultat.isPremiere()).isTrue();
    }

    // obtenirStatsPays

    @Test
    @DisplayName("obtenirStatsPays : succès")
    void obtenirStatsPays_succes() {
        // GIVEN
        Object[] stats = new Object[]{1L, 0L, 0L, 1L, 3L};

        when(paysRepository.existsById(1L)).thenReturn(true);
        when(medailleRepository.getStatsByPays(1L)).thenReturn(stats);
        when(paysRepository.findById(1L)).thenReturn(Optional.of(
                Pays.builder().id(1L).code("SEN").nom("Sénégal").drapeau("🇸🇳").build()
        ));

        // WHEN
        ClassementResponse resultat = medailleService.obtenirStatsPays(1L);

        // THEN
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPaysCode()).isEqualTo("SEN");
        assertThat(resultat.getNbOr()).isEqualTo(1L);
        assertThat(resultat.getPoints()).isEqualTo(3L);
    }

    @Test
    @DisplayName("obtenirStatsPays : échec si pays inexistant")
    void obtenirStatsPays_paysInexistant_lanceResourceNotFoundException() {
        // GIVEN
        when(paysRepository.existsById(999L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class,
                () -> medailleService.obtenirStatsPays(999L));
    }

    // obtenirClassement

    @Test
    @DisplayName("obtenirClassement : par total (défaut)")
    void obtenirClassement_parTotal() {
        // GIVEN
        Object[] row = new Object[]{1L, "Sénégal", "SEN", "🇸🇳", 1L, 0L, 0L, 1L, 3L};
        when(medailleRepository.getClassementParTotal()).thenReturn(List.of(new Object[][]{row}));

        // WHEN
        List<ClassementResponse> resultat = medailleService.obtenirClassement("total");

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getRang()).isEqualTo(1);
        assertThat(resultat.get(0).getPaysCode()).isEqualTo("SEN");
        assertThat(resultat.get(0).getNbOr()).isEqualTo(1L);
        assertThat(resultat.get(0).getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenirClassement : par or")
    void obtenirClassement_parOr() {
        // GIVEN
        Object[] row = new Object[]{1L, "Sénégal", "SEN", "🇸🇳", 2L, 1L, 0L, 3L, 8L};
        when(medailleRepository.getClassementParOr()).thenReturn(List.of(new Object[][]{row}));

        // WHEN
        List<ClassementResponse> resultat = medailleService.obtenirClassement("or");

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNbOr()).isEqualTo(2L);
        assertThat(resultat.get(0).getPoints()).isEqualTo(8L);
    }

    @Test
    @DisplayName("obtenirClassement : par points")
    void obtenirClassement_parPoints() {
        // GIVEN
        Object[] row = new Object[]{1L, "Sénégal", "SEN", "🇸🇳", 1L, 1L, 1L, 3L, 6L};
        when(medailleRepository.getClassementParPoints()).thenReturn(List.of(new Object[][]{row}));

        // WHEN
        List<ClassementResponse> resultat = medailleService.obtenirClassement("points");

        // THEN
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNbArgent()).isEqualTo(1L);
        assertThat(resultat.get(0).getPoints()).isEqualTo(6L);
    }

    @Test
    @DisplayName("obtenirClassement : liste vide si aucune médaille")
    void obtenirClassement_aucuneMedaille_retourneListeVide() {
        // GIVEN
        when(medailleRepository.getClassementParTotal()).thenReturn(List.of(new Object[][]{}));

        // WHEN
        List<ClassementResponse> resultat = medailleService.obtenirClassement("total");

        // THEN
        assertThat(resultat).isEmpty();
    }
}