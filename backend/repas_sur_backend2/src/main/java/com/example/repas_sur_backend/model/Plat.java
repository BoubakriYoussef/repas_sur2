package com.example.repas_sur_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
@Table(name = "plat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"allergenes", "menus"})
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nom;

    private String categorie;

    private String description;

    private boolean contientPorc;

    private boolean estVegetarien;

    @ManyToMany
    @JoinTable(
        name = "plat_allergene",
        joinColumns = @JoinColumn(name = "plat_id"),
        inverseJoinColumns = @JoinColumn(name = "allergene_id")
    )
    private Set<Allergene> allergenes = new HashSet<>();

    @ManyToMany(mappedBy = "plats")
    private Set<Menu> menus = new HashSet<>();
}
