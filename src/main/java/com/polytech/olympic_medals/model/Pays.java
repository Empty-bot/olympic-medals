package com.polytech.olympic_medals.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pays")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code pays est obligatoire")
    @Size(min = 2, max = 3, message = "Le code pays doit contenir 2 ou 3 caractères")
    @Column(nullable = false, unique = true, length = 3)
    private String code;

    @NotBlank(message = "Le nom du pays est obligatoire")
    @Column(nullable = false)
    private String nom;

    @Column(length = 500)
    private String drapeau;

    @OneToMany(mappedBy = "pays", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Athlete> athletes = new ArrayList<>();

    @OneToMany(mappedBy = "pays", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Medaille> medailles = new ArrayList<>();
}