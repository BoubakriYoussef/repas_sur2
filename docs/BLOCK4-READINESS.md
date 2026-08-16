# État de préparation du Bloc 4

Date de l'audit global : 9 août 2026.

## Résultat synthétique

Les sept compétences disposent désormais de leur configuration, de leur
procédure et de validations locales. Les preuves distantes ou humaines ne sont
pas déclarées acquises tant qu'elles n'existent pas réellement dans GitHub,
GitHub Actions, OVHcloud ou un échange avec un utilisateur.

| Compétence | Mise en place locale | Validation disponible | Preuve réelle restant à produire |
|---|---|---|---|
| C4.1.1 — Dépendances | Dependabot npm/Maven/Docker/Actions, audits npm et OWASP | Mise à jour Angular contrôlée, 101 tests frontend, 53 tests backend | PR Dependabot réelle et CI verte ; clé NVD recommandée pour fiabiliser Dependency-Check |
| C4.1.2 — Supervision | Actuator, Micrometer, Prometheus, Grafana, Blackbox, Alertmanager, Mailpit | 3 cibles UP ; incident DOWN/FIRING puis UP/RESOLVED réellement simulé | Captures datées des dashboards, alertes et notifications ; configuration du canal de production |
| C4.2.1 — Anomalies | Issue Form bug, labels documentés, procédure et analyse de cause racine | Anomalie Actuator reproduite et rapport local complet | Issue GitHub réelle avec commentaires, labels et preuves anonymisées |
| C4.2.2 — Correctif | Test de non-régression, correction Actuator et procédure PR/CD | Test rouge puis vert ; suites 53/53 et 101/101 ; runtime sécurisé | Branche/PR dédiée, CI distante, déploiement OVH et validation post-déploiement |
| C4.3.1 — Améliorations | Lighthouse, Prometheus, registre gain/coût/délai et questionnaire | 3 audits stables : performance 66, accessibilité 97, bonnes pratiques 100, SEO 83 | Retours de trois utilisateurs et comparaison avant/après d'une amélioration livrée |
| C4.3.2 — Versions | SemVer, changelog, versions Maven/npm et workflow Release | Préversion cohérente `2.4.0-dev.0`, workflows validés par Actionlint | Finaliser `2.4.0`, créer tag/Release, déployer ce tag et capturer la trace `.deployment` |
| C4.3.3 — Support | Issue Form support, cycle, délais et dossier de synthèse | YAML valide, 12 identifiants uniques et 10 blocs requis | Échange réel, diagnostic collaboratif et confirmation explicite de l'utilisateur |

## Validations de l'audit global

- Docker Compose valide.
- Prometheus valide avec quatre règles d'alerte.
- Alertmanager et dashboard Grafana valides.
- Workflows GitHub Actions valides avec Actionlint.
- Formulaires GitHub valides avec `yq`.
- Backend : 53 tests réussis et packaging Maven réussi.
- Frontend : 101 tests réussis et build de production réussi.
- Audit npm élevé/critique réussi ; trois vulnérabilités modérées transitives
  restent acceptées temporairement.
- Backend, frontend et endpoint de santé vus UP par Prometheus/Blackbox.
- Aucune alerte active après validation.
- Endpoint de santé anonyme sans `components` ni `details`.
- Ports d'administration et backend limités à `127.0.0.1` ; seul le frontend
  publie directement le port 80.
- Aucun secret de validation enregistré dans le dépôt.
- `git diff --check` sans erreur.

## Points bloquants avant les preuves GitHub

Les changements sont encore locaux sur la branche `bloc4`. Il n'existe donc pas
encore de PR Bloc 4, de CI distante associée, de nouvelle GitHub Release ni de
déploiement OVH de la préversion. Le dépôt GitHub étant privé et non accessible
au connecteur utilisé pendant la préparation, ces actions devront être faites
pendant la phase de publication avec un accès GitHub authentifié.

Le scan OWASP local a rencontré la limite publique NVD `HTTP 429`. L'application,
les tests et les builds fonctionnent sans clé, mais un scan Dependency-Check
fiable en CI nécessite de préférence le secret `NVD_API_KEY`. Ne jamais placer
la clé dans un fichier versionné.

## Ordre recommandé avant la rédaction définitive des annexes

1. Publier les changements Bloc 4 dans une PR et obtenir la CI verte.
2. Créer les labels GitHub et vérifier les deux Issue Forms dans l'interface.
3. Réaliser une PR Dependabot contrôlée.
4. Publier l'Issue Actuator et relier la PR corrective.
5. Déployer et valider le correctif sur OVH.
6. Recueillir trois retours utilisateurs et traiter une demande support réelle.
7. Livrer au moins une amélioration mesurée et comparer Lighthouse avant/après.
8. Finaliser le changelog en `2.4.0`, pousser le tag et vérifier la Release.
9. Déployer explicitement `v2.4.0` et conserver les traces de déploiement.
10. Exécuter les commandes de preuve et réaliser les captures pour les annexes.

## Règle de rédaction

Dans le rapport, distinguer systématiquement :

- « mis en place » : configuration ou code présent ;
- « validé localement » : commande ou simulation réussie ;
- « publié » : visible sur GitHub ;
- « déployé » : version exacte confirmée sur OVH ;
- « confirmé par l'utilisateur » : commentaire réel et traçable.

Cette terminologie évite de transformer une intention ou une simulation en
preuve de réalisation.
