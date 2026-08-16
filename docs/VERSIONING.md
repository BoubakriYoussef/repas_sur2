# Versionnement et publication

## Convention

RepasSûr utilise Semantic Versioning à partir de la version 2.4.0 :

- `MAJOR` : changement incompatible nécessitant une migration ;
- `MINOR` : fonctionnalité rétrocompatible ;
- `PATCH` : correction rétrocompatible ;
- `-dev.N` : préversion non déployée, réservée au développement.

Le tag Git et la GitHub Release utilisent `vMAJOR.MINOR.PATCH`. Une préversion,
un commit ou une branche ne doit jamais être présenté comme une version
réellement déployée.

## Source de vérité

Une release est valide seulement si les quatre valeurs suivantes concordent :

1. version du `pom.xml` backend ;
2. version du `package.json` et du `package-lock.json` frontend ;
3. section datée dans `CHANGELOG.md` ;
4. tag Git strict, par exemple `v2.4.0`.

Le workflow `.github/workflows/release.yml` vérifie cette cohérence, rejoue les
tests et builds, puis crée la GitHub Release à partir du changelog.

## Préparer une release

Exemple pour finaliser la préversion actuelle en 2.4.0 :

```powershell
cd backend/repas_sur_backend2
.\mvnw.cmd versions:set "-DnewVersion=2.4.0" -DgenerateBackupPoms=false

cd ../../frontend/repas_sur_frontend2
npm version 2.4.0 --no-git-tag-version
```

À la racine, déplacer le contenu de `[Unreleased]` sous une section datée :

```markdown
## [2.4.0] - 2026-08-20
```

La date doit être la date réelle de publication. Après revue de la PR et CI
verte, fusionner sur `main`, puis créer le tag annoté :

```powershell
git switch main
git pull --ff-only origin main
git tag -a v2.4.0 -m "Release v2.4.0"
git push origin v2.4.0
```

Le push déclenche la validation et la création de la GitHub Release. Ne pas
réutiliser un tag ayant échoué : corriger sur une nouvelle version PATCH.

Une fois la Release verte, lancer manuellement le workflow `Deploy` avec le tag
exact dans le champ `ref`, par exemple `v2.4.0`. Le workflow déploie ce commit en
mode détaché, exécute les smoke tests frontend/backend et enregistre sur le VPS :

- `.deployment/version` ;
- `.deployment/commit` ;
- `.deployment/deployed-at`.

Ces trois fichiers et la sortie du workflow relient la version publiée au code
réellement exécuté. Le déploiement automatique de `main` reste identifié sous la
forme `main@SHA` et ne doit pas être présenté comme une release SemVer.

## Journal des versions réellement déployées

La GitHub Release décrit une version publiée ; le déploiement OVH doit encore
être confirmé. Après validation en production, ajouter au changelog :

- date et environnement du déploiement ;
- SHA du commit et tag ;
- lien vers l'exécution `Deploy` réussie ;
- résultat du smoke test `/actuator/health` et du frontend ;
- Issue et PR pour chaque correctif notable.

Si le déploiement échoue, conserver la release mais la signaler clairement
comme « non déployée » jusqu'à résolution. Cette distinction évite de fabriquer
un historique de production à partir des seuls tags Git.

## Retour au développement

Après une release 2.4.0, choisir la prochaine cible puis utiliser une préversion,
par exemple `2.4.1-dev.0` pour un correctif ou `2.5.0-dev.0` pour une évolution.

## Preuves pour l'annexe

- contenu du `CHANGELOG.md` ;
- tag annoté (`git show v2.4.0`) ;
- GitHub Release et notes associées ;
- workflow Release vert ;
- workflow Deploy vert et écran de production portant la version ;
- réponses des smoke tests après déploiement.
