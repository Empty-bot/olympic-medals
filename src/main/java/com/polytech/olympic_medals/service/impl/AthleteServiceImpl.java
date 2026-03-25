package com.polytech.olympic_medals.service.impl;

import com.polytech.olympic_medals.dto.request.AthleteRequest;
import com.polytech.olympic_medals.dto.response.AthleteResponse;
import com.polytech.olympic_medals.dto.response.PaysResponse;
import com.polytech.olympic_medals.exception.DuplicateResourceException;
import com.polytech.olympic_medals.exception.ResourceNotFoundException;
import com.polytech.olympic_medals.model.Athlete;
import com.polytech.olympic_medals.model.Pays;
import com.polytech.olympic_medals.repository.AthleteRepository;
import com.polytech.olympic_medals.repository.PaysRepository;
import com.polytech.olympic_medals.service.AthleteService;
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
public class AthleteServiceImpl implements AthleteService {

    private final AthleteRepository athleteRepository;
    private final PaysRepository paysRepository;

    @Override
    public AthleteResponse creerAthlete(AthleteRequest request) {
        log.debug("Création d'un athlète : {} {}", request.getPrenom(), request.getNom());

        Pays pays = paysRepository.findById(request.getPaysId())
                .orElseThrow(() -> new ResourceNotFoundException("Pays", request.getPaysId()));

        if (athleteRepository.existsByNomAndPrenomAndPaysId(
                request.getNom(), request.getPrenom(), request.getPaysId())) {
            throw new DuplicateResourceException("Cet athlète existe déjà pour ce pays");
        }

        Athlete athlete = toEntity(request, pays);
        Athlete athleteSauvegarde = athleteRepository.save(athlete);

        log.debug("Athlète créé avec succès, ID : {}", athleteSauvegarde.getId());
        return toResponse(athleteSauvegarde);
    }

    @Override
    @Transactional(readOnly = true)
    public AthleteResponse obtenirAthleteParId(Long id) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlète", id));
        return toResponse(athlete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AthleteResponse> obtenirTousLesAthletes() {
        return athleteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AthleteResponse> obtenirAthleteParPays(Long paysId) {
        if (!paysRepository.existsById(paysId)) {
            throw new ResourceNotFoundException("Pays", paysId);
        }
        return athleteRepository.findByPaysId(paysId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AthleteResponse modifierAthlete(Long id, AthleteRequest request) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlète", id));

        Pays pays = paysRepository.findById(request.getPaysId())
                .orElseThrow(() -> new ResourceNotFoundException("Pays", request.getPaysId()
                ));

        athlete.setNom(request.getNom());
        athlete.setPrenom(request.getPrenom());
        athlete.setDateNaissance(request.getDateNaissance());
        athlete.setDiscipline(request.getDiscipline());
        athlete.setPays(pays);

        return toResponse(athleteRepository.save(athlete));
    }

    @Override
    public void supprimerAthlete(Long id) {
        if (!athleteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Athlète", id);
        }
        athleteRepository.deleteById(id);
        log.debug("Athlète supprimé avec succès, ID : {}", id);
    }

    // Méthodes de conversion privées 

    private Athlete toEntity(AthleteRequest request, Pays pays) {
        return Athlete.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .discipline(request.getDiscipline())
                .pays(pays)
                .build();
    }

    private PaysResponse paysToResponse(Pays pays) {
        return PaysResponse.builder()
                .id(pays.getId())
                .code(pays.getCode())
                .nom(pays.getNom())
                .drapeau(pays.getDrapeau())
                .build();
    }

    private AthleteResponse toResponse(Athlete athlete) {
        return AthleteResponse.builder()
                .id(athlete.getId())
                .nom(athlete.getNom())
                .prenom(athlete.getPrenom())
                .dateNaissance(athlete.getDateNaissance())
                .discipline(athlete.getDiscipline())
                .pays(paysToResponse(athlete.getPays()))
                .build();
    }
}