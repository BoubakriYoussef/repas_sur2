package com.example.repas_sur_backend.model;

import com.example.repas_sur_backend.model.enums.StatutService;
import com.example.repas_sur_backend.model.enums.TypeRepas;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "service_repas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"site", "menu", "alertes"})
public class ServiceRepas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDateTime dateService;

    @Enumerated(EnumType.STRING)
    private TypeRepas typeRepas;

    @Enumerated(EnumType.STRING)
    private StatutService statut;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteRestauration site;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "service")
    private Set<AlerteRisque> alertes = new HashSet<>();
}
