# Création et déploiement d'un correctif — C4.2.2

## Correctif de référence

Le scénario de référence corrige l'exposition de détails techniques par le
endpoint public `/actuator/health`. L'analyse complète se trouve dans
`docs/incidents/2026-08-09-actuator-health-details.md`.

## Séquence Git à publier

Lorsque les changements Bloc 4 précédents auront été intégrés sur la branche de
base, isoler le correctif dans la séquence suivante :

```bash
git switch main
git pull --ff-only origin main
git switch -c fix/actuator-health-details
```

Le commit du correctif doit contenir uniquement la propriété Actuator corrigée,
`ActuatorHealthExposureTest` et la mise à jour du rapport d'incident.

Message recommandé :

```text
fix(security): masque les détails publics d'Actuator Health
```

## Preuve avant/après

Avant correction :

```powershell
mvnw.cmd "-Dtest=ActuatorHealthExposureTest" test
```

Résultat conservé : 1 test, 1 échec, car `$.components` existe. Après correction,
la même commande produit 1 test et 1 réussite.

Validation complète :

```powershell
mvnw.cmd clean verify
```

```bash
npm ci
npm audit --audit-level=high
npm test -- --watch=false --browsers=ChromeHeadless
npm run build -- --configuration=production
```

## Pull request

Titre recommandé :

```text
fix(security): masquer les détails publics d'Actuator Health
```

La description doit contenir le lien `Closes #NUMERO_DU_TICKET`, la cause racine,
la sortie du test avant/après, les résultats des suites de tests et la validation
Prometheus/Blackbox.

## Déploiement et validation

Après fusion sur `main`, `deploy.yml` déploie par SSH sur le VPS si la CI réussit.
La validation post-déploiement utilise :

```bash
curl -s https://DOMAINE/actuator/health
```

Critères : réponse HTTP 200, `status` à `UP`, absence de `components` et `details`
pour une requête anonyme, cibles Prometheus/Blackbox à `UP` et aucune alerte.

## Résultats locaux du 9 août 2026

| Contrôle | Résultat |
|---|---:|
| Test de reproduction avant correction | Échec attendu 1/1 |
| Test de non-régression après correction | Réussi 1/1 |
| Tests backend complets | 53/53 réussis |
| Tests frontend complets | 101/101 réussis |
| Audit npm élevé/critique | Réussi |
| Build Angular production | Réussi |
| Packaging Spring Boot | Réussi |
| Réponse Actuator anonyme | Aucun détail technique |
| Prometheus / Blackbox après redéploiement | UP |
| Alertes actives | 0 |

## Captures à préparer pour les annexes

- ticket GitHub confirmé ;
- branche `fix/actuator-health-details` ;
- test rouge avant correction et test vert après correction ;
- PR et contrôles CI réussis ;
- réponse Actuator avant/après ;
- cibles Prometheus après déploiement ;
- commentaire de validation et ticket clos.
