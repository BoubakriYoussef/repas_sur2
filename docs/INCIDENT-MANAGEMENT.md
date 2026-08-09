# Collecte et qualification des anomalies — C4.2.1

## Objectif

Une anomalie doit contenir suffisamment d'informations pour être reproduite,
qualifiée et diagnostiquée sans dépendre de la mémoire de la personne qui l'a
signalée. GitHub Issues constitue le registre unique des anomalies de RepasSûr.

## Cycle de vie

```text
Nouveau -> À reproduire -> Confirmé -> En correction -> À valider -> Déployé -> Clos
             |                |
             +-> Rejeté       +-> Bloqué
```

Un ticket ne change d'état que lorsqu'un commentaire indique la preuve ou la
raison de la transition.

| Label | Signification | Condition de sortie |
|---|---|---|
| `status: new` | Ticket reçu, non qualifié | Informations initiales contrôlées |
| `status: reproduce` | Reproduction en cours | Reproduit ou déclaré non reproductible |
| `status: confirmed` | Anomalie reproduite | Cause ou zone fautive identifiée |
| `status: fixing` | Correctif en développement | PR et test de non-régression disponibles |
| `status: validate` | Correctif à vérifier | Validation fonctionnelle réussie |
| `status: deployed` | Correctif déployé | Confirmation en production |
| `status: blocked` | Information ou dépendance manquante | Blocage levé |
| `status: rejected` | Doublon, fonctionnement normal ou hors périmètre | Ticket documenté puis clos |

## Criticité

| Niveau | Définition | Délai de prise en charge cible |
|---|---|---:|
| `severity: S1` | Indisponibilité, sécurité ou perte de données | Immédiat |
| `severity: S2` | Fonction essentielle bloquée sans contournement | Moins de 4 h ouvrées |
| `severity: S3` | Gêne avec contournement | Moins de 2 jours ouvrés |
| `severity: S4` | Défaut visuel ou faible impact | Prochaine planification |

La criticité déclarée par l'utilisateur est une estimation. Elle est confirmée
pendant la qualification selon l'impact, le nombre d'utilisateurs touchés, la
fréquence et l'existence d'un contournement.

## Qualification initiale

1. Vérifier la version, l'environnement et l'heure exacte du constat.
2. Rechercher un ticket existant pour éviter les doublons.
3. Contrôler que les données sensibles ont été retirées.
4. Rejouer les étapes avec le même rôle utilisateur.
5. Noter le statut HTTP, l'endpoint et la réponse obtenue.
6. Collecter les logs sur une fenêtre temporelle réduite.
7. Confirmer la criticité et affecter les labels de composant et d'état.

## Commandes de diagnostic

État des services :

```bash
docker compose ps
curl -i http://localhost:8080/actuator/health
```

Logs backend sur les quinze dernières minutes :

```bash
docker compose logs --since 15m --timestamps backend
```

Filtrer les erreurs sous PowerShell :

```powershell
docker compose logs --since 15m --timestamps backend |
  Select-String -Pattern "ERROR|WARN|Exception|Caused by"
```

Reproduire un appel API en conservant le statut et les en-têtes :

```bash
curl -i -X GET http://localhost:8080/api/ressource \
  -H "Authorization: Bearer <TOKEN_MASQUE>"
```

Les tokens ne doivent jamais être copiés dans le ticket. Remplacer toute valeur
sensible par `<MASQUE>`.

## Analyse de cause racine

Après reproduction, créer un document à partir de
`docs/incidents/INCIDENT-TEMPLATE.md`. L'analyse distingue :

- le symptôme visible ;
- l'impact réel ;
- la cause technique immédiate ;
- la cause racine expliquant pourquoi le défaut était possible ;
- les facteurs contributifs ;
- la preuve issue des logs, tests ou métriques ;
- le correctif et les mesures préventives.

La technique des cinq pourquoi peut être utilisée, mais chaque réponse doit être
soutenue par une preuve vérifiable.

## Labels à créer dans GitHub

Labels fonctionnels : `bug`, `component: frontend`, `component: backend`,
`component: database`, `component: deployment`, `component: monitoring`.

Labels de criticité : `severity: S1`, `severity: S2`, `severity: S3`,
`severity: S4`.

Labels d'état : `status: new`, `status: reproduce`, `status: confirmed`,
`status: fixing`, `status: validate`, `status: deployed`, `status: blocked`,
`status: rejected`.

## Preuves pour le jury

- formulaire GitHub Issue visible dans l'interface ;
- ticket réel rempli avec version, environnement et étapes reproductibles ;
- labels de criticité, composant et état ;
- commentaire confirmant la reproduction ;
- extrait de logs anonymisé et daté ;
- analyse de cause racine liée au ticket ;
- historique des changements d'état jusqu'à la clôture.
