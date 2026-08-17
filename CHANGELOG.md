# Journal des modifications

Toutes les modifications notables de RepasSûr sont documentées ici. À partir de
la version 2.4.0, le projet applique Semantic Versioning (`MAJOR.MINOR.PATCH`) et
une structure inspirée de Keep a Changelog.

## [Unreleased]

Aucune modification enregistrée depuis la version 2.4.0.

## [2.4.0] - 2026-08-17

Première version appliquant strictement Semantic Versioning.

### Ajouté

- Supervision complète avec Actuator, Micrometer, Prometheus, Grafana,
  Blackbox Exporter, Alertmanager et Mailpit.
- Règles d'alerte de disponibilité, d'erreurs HTTP 5xx et de latence P95.
- Formulaire GitHub structuré de déclaration d'anomalie et procédure d'incident.
- Audit OWASP Dependency-Check du backend et audit npm dans la CI.
- Audit Lighthouse reproductible et registre priorisé des améliorations.
- Test de non-régression protégeant la confidentialité de `/actuator/health`.

### Modifié

- Mise à jour Angular 20.3 et utilisation de `npm ci` dans l'image frontend.
- Validation backend étendue de `mvn test` à `mvn verify`.

### Sécurité

- Les détails techniques de l'état Actuator ne sont plus exposés aux visiteurs
  non authentifiés.
- Le port direct du backend est limité à l'interface locale de l'hôte ; le
  frontend continue d'y accéder par le réseau Docker interne.

### Documentation

- Guides de gestion des dépendances, supervision, incidents, correctifs,
  améliorations et retours utilisateurs.

### Déploiement

- Version validée et exécutée localement avec Docker Compose.
- Pull Request #37 fusionnée dans `main`.
- Tests backend et frontend réussis.
- Supervision Prometheus, Grafana et Blackbox Exporter validée.

## Historique antérieur à Semantic Versioning strict

Les tags ci-dessous existent dans Git. Leur suffixe `-rncp7` ne suit pas la
convention désormais retenue et leur présence ne prouve pas, à elle seule, un
déploiement en production.

| Tag historique | Date du tag | Contenu principal traçable |
|---|---|---|
| `v2.3-rncp7` | 2026-07-18 | Tests unitaires frontend et backend |
| `v2.1-rncp7` | 2026-07-18 | Ajout de Dependabot |
| `v2-rncp7` | 2026-05-20 | Pipeline CI/CD et préparation du déploiement VPS |
| `v1.2-rncp7` | 2026-03-05 | Tests et documentation |
| `v1.1-rncp7` | 2026-03-05 | Évolutions de la CI |
| `v1.0-rncp7` | 2026-03-04 | Sécurisation et intégration continue |

[Unreleased]: https://github.com/BoubakriYoussef/repas_sur2/compare/v2.4.0...HEAD
[2.4.0]: https://github.com/BoubakriYoussef/repas_sur2/compare/v2.3-rncp7...v2.4.0
