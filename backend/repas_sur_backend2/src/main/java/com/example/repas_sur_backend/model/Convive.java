package com.example.repas_sur_backend.model;

import com.example.repas_sur_backend.model.enums.TypeConvive;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "convive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"site", "allergenes", "regimes", "alertes"})
public class Convive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nom;

    private String prenom;

    @Enumerated(EnumType.STRING)
    private TypeConvive typeConvive;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteRestauration site;

    @ManyToMany
    @JoinTable(
        name = "convive_allergene",
        joinColumns = @JoinColumn(name = "convive_id"),
        inverseJoinColumns = @JoinColumn(name = "allergene_id")
    )
    private Set<Allergene> allergenes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "convive_regime",
        joinColumns = @JoinColumn(name = "convive_id"),
        inverseJoinColumns = @JoinColumn(name = "regime_id")
    )
    private Set<Regime> regimes = new HashSet<>();

    @OneToMany(mappedBy = "convive")
    private Set<AlerteRisque> alertes = new HashSet<>();
}
