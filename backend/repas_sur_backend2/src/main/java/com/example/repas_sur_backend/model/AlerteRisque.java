package com.example.repas_sur_backend.model;

import com.example.repas_sur_backend.model.enums.EtatAlerte;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "alerte_risque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"convive", "service", "actionsCorrectives", "allergenes"})
public class AlerteRisque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private EtatAlerte etat;

    @Enumerated(EnumType.STRING)
    private NiveauAlerte niveau;

    private String message;

    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "convive_id")
    private Convive convive;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceRepas service;

    @OneToMany(mappedBy = "alerte")
    private Set<ActionCorrective> actionsCorrectives = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "alerte_allergene",
        joinColumns = @JoinColumn(name = "alerte_id"),
        inverseJoinColumns = @JoinColumn(name = "allergene_id")
    )
    private Set<Allergene> allergenes = new HashSet<>();
}
