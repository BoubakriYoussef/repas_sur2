# Supervision et alertes — C4.1.2

## Objectif

La supervision vérifie à la fois l'état interne du backend et la disponibilité
réelle des points d'accès de RepasSûr.

```text
Spring Boot Actuator ── métriques ──> Prometheus ──> Grafana
                                         │
Frontend + health API ── Blackbox ───────┤
                                         │ alertes
                                         v
                                    Alertmanager ── SMTP ──> Mailpit
```

Mailpit fournit une boîte de réception locale pour démontrer les notifications
sans publier d'identifiants SMTP personnels. Pour la production, le récepteur
peut être remplacé par le relais SMTP du fournisseur d'hébergement.

## Services et accès

| Service | Adresse locale | Fonction |
|---|---|---|
| Backend Actuator | `http://localhost:8080/actuator/health` | Santé applicative |
| Prometheus | `http://localhost:9090` | Métriques, cibles et règles |
| Grafana | `http://localhost:3000` | Dashboard `RepasSûr Production` |
| Alertmanager | `http://localhost:9093` | Routage et état des alertes |
| Blackbox Exporter | `http://localhost:9115` | Sondes HTTP externes |
| Mailpit | `http://localhost:8025` | Réception des e-mails d'alerte |

Les interfaces de supervision sont liées à `127.0.0.1` dans Docker Compose et
ne sont donc pas exposées publiquement par défaut. Sur un VPS, utiliser un tunnel
SSH pour les consulter.

## Métriques du dashboard

Le dashboard Grafana est provisionné automatiquement et présente :

- l'état du backend ;
- la disponibilité réelle du frontend ;
- le débit des requêtes HTTP ;
- le taux de réponses HTTP 5xx ;
- la latence HTTP P95 ;
- la mémoire JVM utilisée ;
- l'utilisation CPU du backend.

## Règles d'alerte

| Alerte | Condition | Délai | Sévérité |
|---|---|---:|---|
| `RepasSurBackendMetricsDown` | Collecte Actuator impossible | 1 min | Critique |
| `RepasSurEndpointDown` | Frontend ou health API inaccessible | 1 min | Critique |
| `RepasSurHighHttp5xxRate` | Plus de 5 % de réponses 5xx | 5 min | Avertissement |
| `RepasSurHighP95Latency` | P95 supérieur à 1 seconde | 5 min | Avertissement |

Alertmanager envoie aussi une notification `RESOLVED` lorsque le service revient
à la normale.

## Démarrage

Définir notamment `GRAFANA_ADMIN_PASSWORD` dans le fichier `.env`, puis lancer :

```bash
docker compose up -d --build
docker compose ps
```

Vérifications rapides :

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:9090/-/healthy
curl http://localhost:9093/-/healthy
curl "http://localhost:9115/probe?target=http://frontend:80&module=http_2xx"
```

Dans Prometheus, la page **Status > Targets** doit afficher les cibles
`repas-sur-backend` et `blackbox-http` à l'état `UP`.

## Démonstration d'un incident

1. Ouvrir Grafana, Alertmanager et Mailpit.
2. Vérifier que le backend et le frontend sont `UP` dans Grafana.
3. Arrêter le backend :

   ```bash
   docker compose stop backend
   ```

4. Attendre un peu plus d'une minute.
5. Constater dans Prometheus puis Alertmanager que les alertes backend sont actives.
6. Vérifier dans Mailpit la réception de l'e-mail d'indisponibilité.
7. Redémarrer le backend :

   ```bash
   docker compose start backend
   ```

8. Vérifier le retour au vert dans Grafana et la réception de l'e-mail `RESOLVED`.

## Preuves à conserver pour le jury

- capture de `/actuator/health` avec le statut `UP` ;
- capture des cibles Prometheus toutes à `UP` ;
- capture du dashboard `RepasSûr Production` ;
- capture de l'alerte active après arrêt du backend ;
- e-mail d'alerte reçu dans Mailpit ;
- e-mail `RESOLVED` reçu après redémarrage ;
- extraits datés des configurations et commandes de démonstration.

Les fichiers de configuration ont été contrôlés avec les validateurs officiels :
`promtool check config`, `promtool check rules`, `amtool check-config` et
`blackbox_exporter --config.check`.

## Validation réalisée

Test effectué le 9 août 2026 :

- 8 conteneurs démarrés et opérationnels ;
- backend Actuator : `UP` ;
- 3 cibles Prometheus sur 3 à l'état `UP` ;
- 4 règles d'alerte chargées ;
- dashboard `RepasSûr Production` provisionné avec 7 panneaux ;
- arrêt contrôlé du backend ;
- 2 alertes critiques passées de `pending` à `firing` ;
- 2 e-mails `FIRING` reçus dans Mailpit ;
- redémarrage du backend et retour à l'état `UP` ;
- 2 e-mails `RESOLVED` reçus dans Mailpit ;
- aucune alerte encore active après rétablissement.
