# Incident à publier — Exposition de détails techniques dans Actuator Health

## Identification

- Ticket GitHub : à créer après publication du formulaire Issue
- Version : `0.0.1-SNAPSHOT`, branche `bloc4`
- Environnement : local avec Docker Compose
- Date et heure du constat : 2026-08-09 13:58 Europe/Paris
- Criticité confirmée : S3 — Mineure, divulgation technique limitée
- Composant : backend / sécurité
- État : Confirmé

## Symptôme et impact

Une requête non authentifiée vers `/actuator/health` retourne des détails internes
sur PostgreSQL et le système de fichiers du conteneur. Aucun secret ni donnée
métier n'est exposé, mais ces informations facilitent la reconnaissance technique
de l'application.

## Reproduction

### Préconditions

La stack Docker Compose est démarrée. Aucun compte ni token n'est nécessaire.

### Étapes

1. Ouvrir un terminal.
2. Exécuter `curl -i http://localhost:8080/actuator/health`.
3. Observer le corps JSON de la réponse HTTP 200.

### Résultat attendu

Le endpoint public retourne l'état global nécessaire à la sonde, sans détails
techniques ni liste de composants :

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

### Résultat obtenu

La réponse contient notamment :

```json
{
  "components": {
    "db": {"details": {"database": "PostgreSQL"}},
    "diskSpace": {"details": {"path": "/app/.", "total": 1081101176832}}
  },
  "status": "UP"
}
```

Les valeurs de capacité sont données comme preuve ponctuelle et ne contiennent
aucune donnée personnelle.

## Éléments de diagnostic

- Statut HTTP : `200 OK`
- Authentification : aucune
- Configuration concernée :
  `management.endpoint.health.show-details=always`
- Règle de sécurité concernée : `/actuator/health` est autorisé publiquement
- Logs backend : aucune erreur, le comportement vient de la configuration

## Analyse de cause racine

### Cause immédiate

Spring Boot Actuator est configuré pour toujours afficher les détails de santé.

### Cinq pourquoi

1. Les détails sont visibles car `show-details` vaut `always`.
2. Cette valeur a été choisie pour faciliter le diagnostic local.
3. La même configuration est utilisée dans le profil exécuté par Docker.
4. Le endpoint health doit rester public pour Blackbox Exporter.
5. Aucune distinction n'avait été définie entre état global public et diagnostic authentifié.

### Cause racine retenue

Absence de politique séparant la disponibilité publique minimale des détails de
diagnostic réservés aux opérateurs.

### Facteurs contributifs

- configuration commune aux environnements ;
- endpoint explicitement autorisé par Spring Security ;
- absence de test vérifiant le contenu de la réponse publique.

## Décision appliquée

- Remplacer `show-details=always` par `show-details=when-authorized`.
- Conserver le statut global public pour Blackbox Exporter.
- Ajouter un test de non-régression vérifiant l'absence de `components` sans token.
- Vérifier que Prometheus et les sondes restent à l'état `UP` après correction.

## Validation réalisée

- Test avant correction : 1 échec sur 1, présence de `components` confirmée
- Test après correction : 1 réussite sur 1
- Suite backend : 53 tests réussis sur 53
- Suite frontend : 101 tests réussis sur 101
- Build Angular et packaging Spring Boot : réussis
- Réponse Docker : `groups` et `status` uniquement
- Prometheus et Blackbox : cibles backend à l'état `UP`
- Alertes actives après correction : 0
- PR : à créer après publication de la branche dédiée
- CI : commandes équivalentes réussies localement
- Version déployée : à renseigner
- Validation fonctionnelle : aucun `components` ou `details` sans authentification
- Confirmation utilisateur : à renseigner
