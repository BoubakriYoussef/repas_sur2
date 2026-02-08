# Migrations (Liquibase)

Ce projet utilise Liquibase pour gérer les migrations de base de données.

## Regles essentielles
- Ne jamais modifier un changeSet deja applique en base.
- Ajouter un nouveau changeSet pour chaque evolution (colonne, index, contrainte, etc.).
- En dev local, si la base est bloquee, un reset du volume est possible.

## Changelog
Le fichier principal est :
`backend/repas_sur_backend2/src/main/resources/db/changelog/db.changelog-master.xml`

Il est decoupe en changeSets distincts (par table et relations) avec des preConditions
pour eviter les erreurs de type "table already exists".

## Procedure dev (reset base)
Attention : cette operation supprime toutes les donnees.

```
docker compose down -v
docker compose up --build
```

## Procedure prod (sans reset)
- Ajouter un nouveau changeSet dans le changelog.
- Ne jamais toucher aux changeSets existants.
- Deployer la nouvelle version et laisser Liquibase appliquer la migration.
