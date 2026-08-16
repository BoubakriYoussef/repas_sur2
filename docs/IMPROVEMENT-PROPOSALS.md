# Analyse et propositions d'amélioration

Ce document constitue le registre C4.3.1 de RepasSûr. Une proposition n'est
priorisée qu'à partir d'une mesure technique ou d'un retour utilisateur tracé.

## Protocole de mesure

- Date de référence : 9 août 2026.
- Frontend audité : image Docker de production sur `http://localhost`.
- Outil navigateur : trois exécutions Lighthouse avec Chrome en mode headless
  et profil mobile simulé ; la médiane est retenue.
- Backend observé : requêtes instantanées dans Prometheus sur les métriques
  Actuator/Micrometer.
- Les résultats locaux constituent une base comparative, pas un engagement de
  performance de la production OVH. Une décision définitive doit être confirmée
  par plusieurs mesures en production.

Commande reproductible :

```powershell
cd frontend/repas_sur_frontend2
npm run audit:lighthouse
```

Le rapport HTML est généré dans `lighthouse-report.report.html`. Il est ignoré
par Git car il s'agit d'une preuve générée à conserver ou capturer en annexe.

## État de référence

### Lighthouse

| Indicateur | Mesure |
|---|---:|
| Performance | 66/100 |
| Accessibilité | 97/100 |
| Bonnes pratiques | 100/100 |
| SEO | 83/100 |
| First Contentful Paint médian | 3,75 s |
| Largest Contentful Paint médian | 8,70 s |
| Total Blocking Time médian | 74 ms |
| Cumulative Layout Shift | 0 |
| JavaScript potentiellement inutilisé | 173 507 octets |

Constats complémentaires : le logo PNG transféré pèse 966 606 octets pour une
définition de 1254 × 1254 pixels. Lighthouse signale également l'absence d'une
région principale, d'une méta-description et d'un fichier `robots.txt` valide.

### Prometheus

| Indicateur | Mesure instantanée |
|---|---:|
| Backend disponible | 1 (UP) |
| Frontend disponible | 1 (UP) |
| Débit HTTP | 0,137 requête/s |
| Latence HTTP P95 | 16,9 ms |
| Heap JVM utilisé | 68 317 424 octets (environ 65 MiB) |
| CPU du processus backend | 0,000334 (environ 0,03 %) |
| Erreurs HTTP 5xx | aucune série observée |

Le trafic local est faible. Ces valeurs servent principalement à vérifier que
le backend ne présente pas de saturation pendant l'audit ; elles ne remplacent
pas une campagne de charge.

## Registre priorisé

| Priorité | Problème et source | Proposition | Gain attendu | Coût estimé | Délai cible | Critère de validation |
|---|---|---|---|---:|---:|---|
| Haute | LCP 8,7 s ; logo PNG 966 Ko | Redimensionner le logo à sa taille d'affichage, le convertir en WebP/AVIF et déclarer ses dimensions | Réduire le transfert initial d'environ 850 Ko ; viser LCP < 2,5 s | 2 h | Prochaine version mineure | Trois audits : médiane LCP < 2,5 s et image < 120 Ko |
| Haute | 173 507 octets de JavaScript inutilisé ; économie Lighthouse estimée à 900 ms | Charger les écrans métier par routes Angular paresseuses et analyser le bundle | Diminution du bundle initial et amélioration FCP/LCP | 0,5 à 1 j | Prochaine version mineure | Bundle initial réduit d'au moins 25 % et score performance ≥ 85 |
| Haute | Accessibilité 97 ; absence de région principale | Encadrer le contenu principal avec `<main>` et vérifier la navigation clavier | Meilleure navigation avec lecteur d'écran ; viser 100/100 | 1 h | Prochain patch | Audit accessibilité à 100 et test clavier manuel |
| Moyenne | SEO 83 ; méta-description et `robots.txt` absents | Ajouter une description adaptée et un vrai fichier `robots.txt` dans les assets publics | Audit SEO visé à 100/100 | 1 h | Prochain patch | Audit SEO à 100 et `/robots.txt` renvoie du texte valide |
| Moyenne | Mesures Prometheus réalisées sous trafic local très faible | Réaliser une campagne courte de charge reproductible avant dimensionnement OVH | P95 et taux d'erreur représentatifs pour décider de la capacité | 0,5 j | Avant mise en production | Scénario, volume, P95, débit et erreurs archivés |
| À qualifier | Aucun retour utilisateur formalisé à cette date | Faire tester trois parcours avec le questionnaire dédié | Identifier les irritants UX avec fréquence et impact | 2 h | Avant soutenance | Au moins 3 retours anonymisés et une synthèse |

## Règle de décision

Une amélioration passe de « proposition » à « planifiée » lorsqu'elle possède
une source vérifiable, un responsable, un coût estimé et un critère mesurable.
Après livraison, le même protocole est rejoué et les valeurs avant/après sont
ajoutées au registre. Une dégradation entraîne soit un correctif, soit le rejet
documenté de la proposition.

## Preuves prévues pour l'annexe

1. Capture du résumé Lighthouse et des métriques détaillées.
2. Capture du dashboard Grafana sur la même période.
3. Extrait du registre montrant source, gain, coût, priorité et validation.
4. Synthèse anonymisée des retours utilisateurs réels.
5. Comparaison Lighthouse avant/après pour une amélioration effectivement livrée.
