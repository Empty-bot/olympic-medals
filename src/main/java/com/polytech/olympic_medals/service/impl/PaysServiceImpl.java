package com.polytech.olympic_medals.service.impl;

import com.polytech.olympic_medals.dto.request.PaysRequest;
import com.polytech.olympic_medals.dto.response.PaysResponse;
import com.polytech.olympic_medals.exception.DuplicateResourceException;
import com.polytech.olympic_medals.exception.ResourceNotFoundException;
import com.polytech.olympic_medals.model.Pays;
import com.polytech.olympic_medals.repository.PaysRepository;
import com.polytech.olympic_medals.service.PaysService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.polytech.olympic_medals.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaysServiceImpl implements PaysService {

    private final PaysRepository paysRepository;

    @Override
    public PaysResponse creerPays(PaysRequest request) {
        log.debug("Création d'un nouveau pays avec le code : {}", request.getCode());

        if (paysRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                "Un pays avec le code " + request.getCode() + " existe déjà"
            );
        }

        Pays pays = toEntity(request);
        Pays paysSauvegarde = paysRepository.save(pays);

        log.debug("Pays créé avec succès, ID : {}", paysSauvegarde.getId());
        return toResponse(paysSauvegarde);
    }

    @Override
    @Transactional(readOnly = true)
    public PaysResponse obtenirPaysParId(Long id) {
        log.debug("Recherche du pays avec l'ID : {}", id);
        Pays pays = paysRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pays", id));
        return toResponse(pays);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaysResponse> obtenirTousLesPays() {
        log.debug("Récupération de tous les pays");
        return paysRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaysResponse modifierPays(Long id, PaysRequest request) {
        log.debug("Modification du pays avec l'ID : {}", id);
        Pays pays = paysRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pays", id));

        if (!pays.getCode().equals(request.getCode()) &&
                paysRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                "Un pays avec le code " + request.getCode() + " existe déjà"
            );
        }

        pays.setCode(request.getCode());
        pays.setNom(request.getNom());
        pays.setDrapeau(request.getDrapeau());

        Pays paysModifie = paysRepository.save(pays);
        log.debug("Pays modifié avec succès");
        return toResponse(paysModifie);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaysResponse> obtenirTousLesPaysPageable(Pageable pageable) {
        log.debug("Récupération des pays paginés : page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<PaysResponse> page = paysRepository.findAll(pageable)
                .map(this::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public void supprimerPays(Long id) {
        log.debug("Suppression du pays avec l'ID : {}", id);
        if (!paysRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pays", id);
        }
        paysRepository.deleteById(id);
        log.debug("Pays supprimé avec succès");
    }

    // Méthodes de conversion privées 

    private Pays toEntity(PaysRequest request) {
        return Pays.builder()
                .code(request.getCode())
                .nom(request.getNom())
                .drapeau(request.getDrapeau())
                .build();
    }

    private PaysResponse toResponse(Pays pays) {
        return PaysResponse.builder()
                .id(pays.getId())
                .code(pays.getCode())
                .nom(pays.getNom())
                .drapeau(pays.getDrapeau())
                .build();
    }
}