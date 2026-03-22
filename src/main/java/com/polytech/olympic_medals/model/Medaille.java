package com.polytech.olympic_medals.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medailles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medaille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le type de médaille est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMedaille type;

    @Column
    private LocalDate dateObtention;

    @NotNull(message = "L'athlète est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Athlete athlete;

    @NotNull(message = "Le pays est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pays_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pays pays;

    @NotNull(message = "La compétition est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Competition competition;
}