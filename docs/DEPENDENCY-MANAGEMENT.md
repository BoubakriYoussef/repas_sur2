# Gestion des dépendances — C4.1.1

## Périmètre surveillé

Dependabot contrôle chaque semaine :

- les dépendances npm du frontend ;
- les dépendances Maven du backend ;
- les images Docker de la racine, du backend et du frontend ;
- les actions utilisées par GitHub Actions.

La configuration se trouve dans `.github/dependabot.yml`.

## Contrôles automatiques

Chaque pull request vers `main` déclenche la CI :

1. `npm ci` installe exactement les versions du `package-lock.json` ;
2. `npm audit --audit-level=high` bloque les vulnérabilités hautes ou critiques ;
3. `./mvnw clean verify` exécute les tests backend et OWASP Dependency-Check ;
4. Dependency-Check bloque la construction à partir d'un score CVSS de 7 ;
5. les rapports HTML et JSON sont conservés comme artefacts GitHub Actions.

Une clé NVD peut être enregistrée dans le secret GitHub `NVD_API_KEY`. Elle n'est
pas obligatoire, mais accélère et fiabilise la récupération des données de
vulnérabilités.

## Vérification locale

Frontend :

```bash
cd frontend/repas_sur_frontend2
npm ci
npm audit --audit-level=high
npm test -- --watch=false --browsers=ChromeHeadless
npm run build -- --configuration=production
```

Backend :

```bash
cd backend/repas_sur_backend2
./mvnw clean verify
```

Sous PowerShell, utiliser `mvnw.cmd clean verify`.

Les rapports OWASP sont produits dans `backend/repas_sur_backend2/target/`.

## Première mise à jour contrôlée

Le 9 août 2026, le premier audit npm a détecté 41 vulnérabilités : 1 faible,
12 modérées, 27 élevées et 1 critique. Les correctifs compatibles et la mise à
jour corrective d'Angular 20 ont ramené le résultat à 3 vulnérabilités modérées.

Versions Angular mises à jour :

- framework : `20.3.15` vers `20.3.27` ;
- builder : `20.3.15` vers `20.3.33` ;
- CLI résolu : `20.3.33`.

Les trois alertes restantes proviennent de `@hono/node-server`, dépendance
transitive du CLI Angular utilisée uniquement pour le développement. npm ne
propose leur correction qu'en migrant vers Angular CLI 21, ce qui constitue un
changement majeur. Le risque est donc accepté temporairement : aucun paquet de
production n'est concerné, la CI bloque toujours toute nouvelle vulnérabilité
élevée ou critique, et Dependabot signalera une correction compatible future.

Résultats de non-régression :

- `npm audit --audit-level=high` : réussi ;
- tests Angular avec ChromeHeadless : 101 réussis sur 101 ;
- build Angular de production : réussi ;
- tests backend : 53 réussis sur 53 ;
- packaging Spring Boot : réussi ;
- rapport JaCoCo : généré sur 63 classes.

Le premier téléchargement local de la base NVD a été interrompu par la limite
publique `HTTP 429`. Une clé NVD doit être configurée dans la variable
`NVD_API_KEY` en local ainsi que dans les secrets GitHub Actions et Dependabot
pour rendre le scan OWASP fiable, notamment sur les PR créées par Dependabot.

## Traitement d'une mise à jour Dependabot

1. Lire la note de version et identifier les changements incompatibles.
2. Vérifier la dépendance directe ou transitive concernée et la vulnérabilité corrigée.
3. Contrôler le résultat des tests, de la compilation et des audits dans la PR.
4. Tester manuellement la fonction impactée lorsque la dépendance touche le comportement applicatif.
5. Fusionner uniquement si tous les contrôles réussissent ; sinon documenter le rejet ou reporter la mise à jour.

## Preuve à conserver pour le jury

Pour une vraie PR Dependabot, conserver :

- la PR et les versions avant/après ;
- le lien vers la note de version ou l'avis de sécurité ;
- la capture des contrôles CI réussis ;
- le rapport d'audit associé ;
- la conclusion de l'analyse de risque et le résultat du test fonctionnel ciblé.
