package com.polytech.olympic_medals.repository;

import com.polytech.olympic_medals.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    List<Athlete> findByPaysId(Long paysId);

    List<Athlete> findByDiscipline(String discipline);

    boolean existsByNomAndPrenomAndPaysId(String nom, String prenom, Long paysId);
}