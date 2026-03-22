package com.polytech.olympic_medals.repository;

import com.polytech.olympic_medals.model.Medaille;
import com.polytech.olympic_medals.model.TypeMedaille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedailleRepository extends JpaRepository<Medaille, Long> {

    List<Medaille> findByAthleteId(Long athleteId);

    List<Medaille> findByCompetitionId(Long competitionId);

    List<Medaille> findByPaysId(Long paysId);

    long countByPaysIdAndType(Long paysId, TypeMedaille type);

    @Query("""
        SELECT m.pays.id, m.pays.nom, m.pays.code, m.pays.drapeau,
               SUM(CASE WHEN m.type = 'OR'     THEN 1 ELSE 0 END) as nbOr,
               SUM(CASE WHEN m.type = 'ARGENT' THEN 1 ELSE 0 END) as nbArgent,
               SUM(CASE WHEN m.type = 'BRONZE' THEN 1 ELSE 0 END) as nbBronze,
               COUNT(m.id) as total,
               SUM(CASE WHEN m.type = 'OR'     THEN 3
                        WHEN m.type = 'ARGENT' THEN 2
                        WHEN m.type = 'BRONZE' THEN 1
                        ELSE 0 END) as points
        FROM Medaille m
        GROUP BY m.pays.id, m.pays.nom, m.pays.code, m.pays.drapeau
        ORDER BY nbOr DESC, nbArgent DESC, nbBronze DESC
    """)
    List<Object[]> getClassementParOr();

    @Query("""
        SELECT m.pays.id, m.pays.nom, m.pays.code, m.pays.drapeau,
               SUM(CASE WHEN m.type = 'OR'     THEN 1 ELSE 0 END) as nbOr,
               SUM(CASE WHEN m.type = 'ARGENT' THEN 1 ELSE 0 END) as nbArgent,
               SUM(CASE WHEN m.type = 'BRONZE' THEN 1 ELSE 0 END) as nbBronze,
               COUNT(m.id) as total,
               SUM(CASE WHEN m.type = 'OR'     THEN 3
                        WHEN m.type = 'ARGENT' THEN 2
                        WHEN m.type = 'BRONZE' THEN 1
                        ELSE 0 END) as points
        FROM Medaille m
        GROUP BY m.pays.id, m.pays.nom, m.pays.code, m.pays.drapeau
        ORDER BY total DESC
    """)
    List<Object[]> getClassementParTotal();

    @Query("""
        SELECT m.pays.id, m.pays.nom, m.pays.code, m.pays.drapeau,
               SUM(CASE WHEN m.type = 'OR'     THEN 1 ELSE 0 END) as nbOr,
               SUM(CASE WHEN m.type = 'ARGENT' THEN 1 ELSE 0 END) as nbArgent,
               SUM(CASE WHEN m.type = 'BRONZE' THEN 1 ELSE 0 END) as nbBronze,
               COUNT(m.id) as total,
               SUM(CASE WHEN m.type = 'OR'     THEN 3
                        WHEN m.type = 'ARGENT' THEN 2
                        WHEN m.type = 'BRONZE' THEN 1
                        ELSE 0 END) as points
        FROM Medaille m
        GROUP BY m.pays.id, m.pays.nom, m.pays.code, m.pays.drapeau
        ORDER BY points DESC
    """)
    List<Object[]> getClassementParPoints();

    @Query("""
        SELECT SUM(CASE WHEN m.type = 'OR'     THEN 1 ELSE 0 END),
               SUM(CASE WHEN m.type = 'ARGENT' THEN 1 ELSE 0 END),
               SUM(CASE WHEN m.type = 'BRONZE' THEN 1 ELSE 0 END),
               COUNT(m.id),
               SUM(CASE WHEN m.type = 'OR'     THEN 3
                        WHEN m.type = 'ARGENT' THEN 2
                        WHEN m.type = 'BRONZE' THEN 1
                        ELSE 0 END)
        FROM Medaille m
        WHERE m.pays.id = :paysId
    """)
    Object[] getStatsByPays(@Param("paysId") Long paysId);
}