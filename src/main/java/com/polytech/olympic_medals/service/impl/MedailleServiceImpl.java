package com.polytech.olympic_medals.service.impl;

import com.polytech.olympic_medals.dto.request.MedailleRequest;
import com.polytech.olympic_medals.dto.response.*;
import com.polytech.olympic_medals.exception.ResourceNotFoundException;
import com.polytech.olympic_medals.model.*;
import com.polytech.olympic_medals.repository.*;
import com.polytech.olympic_medals.service.MedailleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedailleServiceImpl implements MedailleService {

    private final MedailleRepository medailleRepository;
    private final AthleteRepository athleteRepository;
    private final PaysRepository paysRepository;
    private final CompetitionRepository competitionRepository;

    @Override
    public MedailleResponse enregistrerMedaille(MedailleRequest request) {
        log.debug("Enregistrement d'une médaille de type : {}", request.getType());

        Athlete athlete = athleteRepository.findById(request.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlète", request.getAthleteId()
                ));

        Pays pays = paysRepository.findById(request.getPaysId())
                .orElseThrow(() -> new ResourceNotFoundException("Pays", request.getPaysId()
                ));

        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Compétition", request.getCompetitionId()
                ));

        Medaille medaille = Medaille.builder()
                .type(request.getType())
                .dateObtention(request.getDateObtention())
                .athlete(athlete)
                .pays(pays)
                .competition(competition)
                .build();

        Medaille saved = medailleRepository.save(medaille);
        log.debug("Médaille enregistrée avec succès, ID : {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MedailleResponse obtenirMedailleParId(Long id) {
        Medaille medaille = medailleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médaille", id
                ));
        return toResponse(medaille);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedailleResponse> obtenirToutesLesMedailles() {
        return medailleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedailleResponse> obtenirMedaillesParAthlete(Long athleteId) {
        if (!athleteRepository.existsById(athleteId)) {
            throw new ResourceNotFoundException("Athlète", athleteId);
        }
        return medailleRepository.findByAthleteId(athleteId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedailleResponse> obtenirMedaillesParCompetition(Long competitionId) {
        if (!competitionRepository.existsById(competitionId)) {
            throw new ResourceNotFoundException("Compétition", competitionId);
        }
        return medailleRepository.findByCompetitionId(competitionId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassementResponse> obtenirClassement(String tri) {
        log.debug("Calcul du classement avec le tri : {}", tri);

        List<Object[]> resultats = switch (tri) {
            case "or"     -> medailleRepository.getClassementParOr();
            case "points" -> medailleRepository.getClassementParPoints();
            default       -> medailleRepository.getClassementParTotal();
        };

        List<ClassementResponse> classement = new ArrayList<>();
        for (int i = 0; i < resultats.size(); i++) {
            classement.add(toClassementResponse(resultats.get(i), i + 1));
        }
        return classement;
    }

    @Override
    @Transactional(readOnly = true)
    public ClassementResponse obtenirStatsPays(Long paysId) {
        if (!paysRepository.existsById(paysId)) {
            throw new ResourceNotFoundException("Pays", paysId);
        }

        Object[] stats = medailleRepository.getStatsByPays(paysId);
        Pays pays = paysRepository.findById(paysId)
            .orElseThrow(() -> new ResourceNotFoundException("Pays", paysId));

        return ClassementResponse.builder()
                .paysId(paysId)
                .paysNom(pays.getNom())
                .paysCode(pays.getCode())
                .paysDrapeau(pays.getDrapeau())
                .nbOr(stats[0] != null     ? ((Number) stats[0]).longValue() : 0)
                .nbArgent(stats[1] != null ? ((Number) stats[1]).longValue() : 0)
                .nbBronze(stats[2] != null ? ((Number) stats[2]).longValue() : 0)
                .total(stats[3] != null    ? ((Number) stats[3]).longValue() : 0)
                .points(stats[4] != null   ? ((Number) stats[4]).longValue() : 0)
                .build();
    }

    // Méthodes de conversion privées

    private MedailleResponse toResponse(Medaille medaille) {
        return MedailleResponse.builder()
                .id(medaille.getId())
                .type(medaille.getType())
                .dateObtention(medaille.getDateObtention())
                .athlete(AthleteResponse.builder()
                        .id(medaille.getAthlete().getId())
                        .nom(medaille.getAthlete().getNom())
                        .prenom(medaille.getAthlete().getPrenom())
                        .discipline(medaille.getAthlete().getDiscipline())
                        .pays(PaysResponse.builder()
                                .id(medaille.getAthlete().getPays().getId())
                                .nom(medaille.getAthlete().getPays().getNom())
                                .code(medaille.getAthlete().getPays().getCode())
                                .build())
                        .build())
                .pays(PaysResponse.builder()
                        .id(medaille.getPays().getId())
                        .nom(medaille.getPays().getNom())
                        .code(medaille.getPays().getCode())
                        .drapeau(medaille.getPays().getDrapeau())
                        .build())
                .competition(CompetitionResponse.builder()
                        .id(medaille.getCompetition().getId())
                        .nom(medaille.getCompetition().getNom())
                        .discipline(medaille.getCompetition().getDiscipline())
                        .statut(medaille.getCompetition().getStatut())
                        .build())
                .build();
    }

    private ClassementResponse toClassementResponse(Object[] row, int rang) {
        return ClassementResponse.builder()
                .rang(rang)
                .paysId(((Number) row[0]).longValue())
                .paysNom((String) row[1])
                .paysCode((String) row[2])
                .paysDrapeau((String) row[3])
                .nbOr(((Number) row[4]).longValue())
                .nbArgent(((Number) row[5]).longValue())
                .nbBronze(((Number) row[6]).longValue())
                .total(((Number) row[7]).longValue())
                .points(((Number) row[8]).longValue())
                .build();
    }
}