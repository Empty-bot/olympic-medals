package com.polytech.olympic_medals.repository;

import com.polytech.olympic_medals.model.Pays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaysRepository extends JpaRepository<Pays, Long> {

    Optional<Pays> findByCode(String code);

    Optional<Pays> findByNom(String nom);

    boolean existsByCode(String code);
}