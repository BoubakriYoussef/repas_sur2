package com.example.repas_sur_backend.model;

import com.example.repas_sur_backend.model.enums.TypeSite;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "site_restauration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"convives", "services", "utilisateurs"})
public class SiteRestauration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSite type;

    private String adresse;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private Set<Convive> convives = new HashSet<>();

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private Set<ServiceRepas> services = new HashSet<>();

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private Set<Utilisateur> utilisateurs = new HashSet<>();
}
