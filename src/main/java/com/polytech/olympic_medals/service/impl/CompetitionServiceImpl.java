package com.polytech.olympic_medals.service.impl;

import com.polytech.olympic_medals.dto.request.CompetitionRequest;
import com.polytech.olympic_medals.dto.response.CompetitionResponse;
import com.polytech.olympic_medals.model.Competition;
import com.polytech.olympic_medals.model.StatutCompetition;
import com.polytech.olympic_medals.repository.CompetitionRepository;
import com.polytech.olympic_medals.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    @Override
    public CompetitionResponse creerCompetition(CompetitionRequest request) {
        log.debug("Création d'une compétition : {}", request.getNom());

        Competition competition = toEntity(request);
        Competition saved = competitionRepository.save(competition);

        log.debug("Compétition créée avec succès, ID : {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CompetitionResponse obtenirCompetitionParId(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Compétition non trouvée avec l'ID : " + id
                ));
        return toResponse(competition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitionResponse> obtenirToutesLesCompetitions() {
        return competitionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompetitionResponse modifierCompetition(Long id, CompetitionRequest request) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Compétition non trouvée avec l'ID : " + id
                ));

        competition.setNom(request.getNom());
        competition.setDiscipline(request.getDiscipline());
        competition.setDateDebut(request.getDateDebut());
        competition.setDateFin(request.getDateFin());

        if (request.getStatut() != null) {
            competition.setStatut(request.getStatut());
        }

        return toResponse(competitionRepository.save(competition));
    }

    @Override
    public void supprimerCompetition(Long id) {
        if (!competitionRepository.existsById(id)) {
            throw new RuntimeException("Compétition non trouvée avec l'ID : " + id);
        }
        competitionRepository.deleteById(id);
        log.debug("Compétition supprimée avec succès, ID : {}", id);
    }

    // Méthodes de conversion privées

    private Competition toEntity(CompetitionRequest request) {
        return Competition.builder()
                .nom(request.getNom())
                .discipline(request.getDiscipline())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .statut(request.getStatut() != null
                        ? request.getStatut()
                        : StatutCompetition.PLANIFIEE)
                .build();
    }

    private CompetitionResponse toResponse(Competition competition) {
        return CompetitionResponse.builder()
                .id(competition.getId())
                .nom(competition.getNom())
                .discipline(competition.getDiscipline())
                .dateDebut(competition.getDateDebut())
                .dateFin(competition.getDateFin())
                .statut(competition.getStatut())
                .build();
    }
}