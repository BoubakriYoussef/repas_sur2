package com.example.repas_sur_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "allergene")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"plats", "convives", "alertes"})
public class Allergene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String code;

    private String libelle;

    private String description;

    @ManyToMany(mappedBy = "allergenes")
    private Set<Plat> plats = new HashSet<>();

    @ManyToMany(mappedBy = "allergenes")
    private Set<Convive> convives = new HashSet<>();

    @ManyToMany(mappedBy = "allergenes")
    private Set<AlerteRisque> alertes = new HashSet<>();
}
