# Collaboration avec le support — C4.3.3

## Objectif

Le support de RepasSûr doit aider l'utilisateur à atteindre son objectif, garder
une trace du diagnostic et obtenir une confirmation explicite avant la clôture.
GitHub Issues constitue le registre de référence. Un échange oral, par mail ou
Teams peut accélérer le diagnostic, mais sa synthèse anonymisée doit être ajoutée
au ticket avec l'accord de l'utilisateur.

## Canaux et confidentialité

- Demande fonctionnelle : formulaire GitHub « Demander de l'aide ».
- Anomalie déjà reproductible : formulaire GitHub « Signaler une anomalie ».
- Vulnérabilité ou secret : GitHub Security Advisory privé, jamais une issue.
- Échange synchrone : Teams ou entretien, puis compte rendu dans l'issue.

Ne jamais enregistrer dans GitHub un nom complet, une adresse électronique, un
token, un mot de passe ou une donnée personnelle ou médicale. Le demandeur est
identifié dans les preuves par un pseudonyme, par exemple `Utilisateur-T01`.

## Cycle de traitement

```text
Demande reçue -> Qualification -> Diagnostic collaboratif -> Réponse ou correctif
                     |                                      |
                     +-> informations manquantes            v
                                                   Validation utilisateur
                                                            |
                                                            v
                                                          Clos
```

| État | Preuve attendue |
|---|---|
| `status: new` | Formulaire reçu avec version, objectif et impact |
| `support: qualifying` | Questions complémentaires publiées |
| `support: investigating` | Hypothèse et vérification en cours |
| `status: fixing` | Issue bug liée, branche et test si un défaut est confirmé |
| `support: user-validation` | Solution livrée et procédure de vérification communiquée |
| `status: resolved` | Confirmation explicite de l'utilisateur ou règle d'inactivité appliquée |

## Engagements cibles

| Impact déclaré | Première réponse cible | Mise à jour cible |
|---|---:|---:|
| Bloquant | 4 heures ouvrées | Chaque jour ouvré |
| Important | 1 jour ouvré | Tous les 2 jours ouvrés |
| Modéré | 2 jours ouvrés | Hebdomadaire |
| Faible | 5 jours ouvrés | À chaque changement d'état |

Ces délais sont des objectifs adaptés à un projet personnel, pas une garantie
contractuelle de service.

## Première réponse

Le premier commentaire du support doit :

1. reformuler l'objectif de l'utilisateur ;
2. confirmer la version, le rôle et l'environnement ;
3. demander uniquement les informations encore manquantes ;
4. proposer une vérification courte et observable ;
5. annoncer la prochaine étape et le prochain point de suivi.

Modèle :

```markdown
Merci pour ce signalement. Je comprends que votre objectif est **[objectif]**
sur **[version/environnement]** avec le rôle **[rôle]**.

Pour distinguer un problème d'utilisation d'une anomalie, pouvez-vous confirmer :
1. [question précise] ;
2. [résultat d'une action courte] ;
3. [heure exacte si les logs sont nécessaires] ?

Ne transmettez aucun token ni donnée personnelle. Je reviens vers vous après
[prochaine vérification] au plus tard [échéance].
```

## Diagnostic collaboratif

Chaque hypothèse doit être accompagnée d'un test et de son résultat. Si un bug
est confirmé :

1. conserver l'issue support comme origine du retour ;
2. créer ou lier une issue `bug` avec les étapes reproductibles ;
3. relier la PR et le test de non-régression ;
4. communiquer un éventuel contournement ;
5. demander au même utilisateur de valider la version corrigée.

Si le comportement est normal, expliquer la procédure avec des étapes adaptées
au rôle de l'utilisateur et vérifier qu'il atteint effectivement son objectif.

## Résolution et clôture

Modèle de commentaire de validation :

```markdown
La solution est disponible dans **[version]** sur **[environnement]**.

Merci de vérifier :
1. [étape] ;
2. [étape] ;
3. résultat attendu : **[résultat]**.

Pouvez-vous confirmer que votre objectif initial est atteint et qu'aucun autre
blocage n'apparaît ?
```

Le ticket est clos lorsque l'utilisateur confirme la résolution. Sans réponse,
effectuer une relance après cinq jours ouvrés, puis clore après dix jours
ouvrés en indiquant « clôture administrative sans validation utilisateur ». Ce
cas ne doit pas être présenté au jury comme une résolution confirmée.

## Labels à créer dans GitHub

- `support` ;
- `support: qualifying` ;
- `support: investigating` ;
- `support: user-validation` ;
- `status: resolved` ;
- les labels de composant, criticité et correctif définis pour les incidents.

## Preuve réelle attendue

Utiliser `docs/support/SUPPORT-CASE-TEMPLATE.md` pour préparer la synthèse, puis
présenter en annexe :

1. la demande réelle anonymisée ;
2. les questions du support et les réponses de l'utilisateur ;
3. les preuves du diagnostic ;
4. l'Issue ou la PR liée si une correction était nécessaire ;
5. le message de mise à disposition ;
6. la confirmation explicite du même utilisateur ;
7. les dates montrant le respect du délai de réponse cible.

Un scénario inventé ne constitue pas une preuve C4.3.3.
