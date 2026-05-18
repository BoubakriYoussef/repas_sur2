# Deploiement continu sur VPS OVH

## Vue d'ensemble

Le projet peut etre deploie simplement sur un VPS avec Docker Compose.

- GitHub Actions lance deja la CI.
- Un workflow `deploy.yml` a ete ajoute pour deployer automatiquement sur le VPS apres une CI verte sur `main`.
- Le frontend en production appelle maintenant l'API via `/api`, ce qui passe par le proxy Nginx du conteneur frontend.

## 1. Preparation du VPS

Se connecter en SSH puis installer les prerequis :

```bash
sudo apt update
sudo apt install -y git docker.io docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
```

Reconnecte-toi ensuite en SSH pour que le groupe `docker` soit pris en compte.

## 2. Premiere installation sur le VPS

Choisis un dossier applicatif, par exemple `/opt/repas_sur2`.

```bash
sudo mkdir -p /opt/repas_sur2
sudo chown -R $USER:$USER /opt/repas_sur2
git clone <URL_DE_TON_REPO> /opt/repas_sur2
cd /opt/repas_sur2
cp .env.example .env
```

Edite ensuite `.env` avec tes vraies valeurs :

```dotenv
POSTGRES_DB=repas_sur
POSTGRES_USER=repas_user
POSTGRES_PASSWORD=mot_de_passe_fort
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=secret_base64_long
JWT_EXPIRATION_MS=86400000
```

Demarrage initial :

```bash
docker compose --env-file .env -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```

## 3. Secrets GitHub a ajouter

Dans `GitHub > Settings > Secrets and variables > Actions`, ajoute :

- `SSH_HOST` : IP publique du VPS
- `SSH_PORT` : `22` si tu n'as rien change
- `SSH_USER` : utilisateur SSH du VPS
- `SSH_PRIVATE_KEY` : cle privee correspondant a la cle publique presente sur le VPS
- `APP_DIR` : `/opt/repas_sur2`

Si le depot GitHub est prive, le VPS devra aussi avoir un acces `git clone`/`git pull` valide sur le repo via une deploy key ou une cle SSH autorisee.

## 4. Comportement du deploiement

Le workflow de deploiement :

- s'execute apres la workflow `CI`
- ne deploie que si la CI a reussi
- ne deploie automatiquement que pour un `push` sur `main`
- fait un `git pull` sur le VPS
- relance les conteneurs avec `docker compose up -d --build`

## 5. Ports exposes

En production :

- le frontend est expose sur le port `80`
- le backend est lie a `127.0.0.1:8080` et n'est donc pas expose publiquement
- PostgreSQL reste interne au reseau Docker

## 6. HTTPS recommande

La prochaine etape propre est d'ajouter un nom de domaine et HTTPS.

Le plus simple ensuite est :

- soit un Nginx ou Caddy sur l'hote
- soit un reverse proxy Docker dedie

## 7. Commandes utiles sur le VPS

```bash
cd /opt/repas_sur2
docker compose -f docker-compose.yml -f docker-compose.prod.yml ps
docker compose -f docker-compose.yml -f docker-compose.prod.yml logs -f
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```
