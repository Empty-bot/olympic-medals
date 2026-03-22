package com.polytech.olympic_medals.repository;

import com.polytech.olympic_medals.model.Competition;
import com.polytech.olympic_medals.model.StatutCompetition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    List<Competition> findByStatut(StatutCompetition statut);

    List<Competition> findByDiscipline(String discipline);
}