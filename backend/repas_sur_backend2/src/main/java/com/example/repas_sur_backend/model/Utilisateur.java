package com.example.repas_sur_backend.model;

import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
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
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"site", "actionsCorrectives"})
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String username;

    private String password;

    private String email;

    private String telephone;

    private String poste;

    @Enumerated(EnumType.STRING)
    private RoleUtilisateur role;

    private boolean actif;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteRestauration site;

    @OneToMany(mappedBy = "utilisateur")
    private Set<ActionCorrective> actionsCorrectives = new HashSet<>();
}
