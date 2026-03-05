# RepasSûr

[![CI](https://github.com/BoubakriYoussef/repas_sur2/actions/workflows/ci.yml/badge.svg)](https://github.com/BoubakriYoussef/repas_sur2/actions/workflows/ci.yml)

Application web fullstack pour la restauration collective, visant a gerer les profils allergiques des convives et a reduire les risques lies aux allergenes dans les menus servis.

## Contexte
Projet realisé dans le cadre d'une certification RNCP niveau 7 (Expert en Developpement Logiciel – M2 Developpement Fullstack).

## Objectif
Permettre la gestion centralisee des profils convives, des plats et des allergenes, et evaluer automatiquement les risques allergenes lors de la planification des services (repas collectifs).

## Stack technique
- Backend : Java 21, Spring Boot, Spring Data JPA, Hibernate
- Base de donnees : PostgreSQL
- Frontend : Angular 20
- Conteneurisation : Docker, Docker Compose
- Outils : Maven, Lombok, Bean Validation, Spring Security (JWT)

## Fonctionnalites cles
- Gestion des convives (CRUD, allergies, regimes)
- Referentiel d'allergenes
- Gestion des plats, menus et services
- Evaluation automatique des risques allergenes
- Tableau de bord des alertes
- (Optionnel) Microservice IA de recommandation de menus

## Architecture
- Architecture 3-tiers : frontend / backend / base de donnees
- API REST (backend)
- Modele JPA centre sur : Convive, Allergene, Plat, Menu, Service, Alerte

## Demarrage rapide (Docker)
```bash
docker compose up -d
```

## Acces
- Frontend : http://localhost
- Backend : http://localhost:8080
- Swagger/OpenAPI : http://localhost:8080/swagger-ui/index.html

## Donnees de demonstration (Liquibase)
La base est pre-remplie via Liquibase avec des sites, convives, allergenes, plats, menus, services, alertes et actions correctives.

Comptes de connexion de demonstration :
- `admin.demo` / `Admin123!` (ADMIN)
- `responsable.demo` / `Resp123!` (RESPONSABLE)
- `cuisine.demo` / `Cuisine123!` (CUISINE)

## Tests
Execution rapide depuis la racine du projet.

### Backend - Local (Maven installe)
```bash
cd backend/repas_sur_backend2
./mvnw test
```

Sur Windows PowerShell :
```powershell
cd backend\repas_sur_backend2
.\mvnw.cmd test
```

### Backend - Docker (sans Java/Maven local)
Depuis Git Bash :
```bash
docker run --rm \
  -v "/c/Users/<USER>/Documents/Repos/rncp7/repas_sur2/backend/repas_sur_backend2:/app" \
  -w /app \
  maven:3.9.6-eclipse-temurin-21 \
  ./mvnw test
```

Depuis PowerShell :
```powershell
docker run --rm -v "C:\Users\<USER>\Documents\Repos\rncp7\repas_sur2\backend\repas_sur_backend2:/app" -w /app maven:3.9.6-eclipse-temurin-21 ./mvnw test
```

### Frontend - Local (Node.js installe)
```bash
cd frontend/repas_sur_frontend2
npm ci
npm run test -- --watch=false --browsers=ChromeHeadless
```

### Frontend - CI (ce qui est execute dans GitHub Actions)
```bash
cd frontend/repas_sur_frontend2
npm ci
npm run test -- --watch=false --browsers=ChromeHeadless
npm run build -- --configuration=production
```

## Documentation
- Architecture :
![](docs/images/architecture.png)

- Diagramme UML des classes : 
![](docs/images/uml-classes.png)

- Tests unitaires / integration : a completer

## Licence
Projet academique.
