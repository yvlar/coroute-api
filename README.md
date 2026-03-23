# 🚗 CoRoute API

![CI](https://github.com/TON-USERNAME/coroute-api/actions/workflows/ci.yml/badge.svg)
![Docker](https://github.com/TON-USERNAME/coroute-api/actions/workflows/ci.yml/badge.svg?job=docker)

> Plateforme de covoiturage québécoise — API REST sécurisée avec authentification JWT, persistance MongoDB et déploiement Docker.

---

## ✨ Fonctionnalités

- 🔐 **Authentification JWT** — Inscription, connexion, tokens sécurisés
- 🔑 **Mots de passe hashés** — BCrypt pour la sécurité des comptes
- 🚗 **Gestion des trajets** — Créer, chercher, filtrer et supprimer des trajets
- 🎫 **Réservations** — Réserver et annuler des places sur un trajet
- 🗄️ **MongoDB** — Persistance des données avec Morphia ODM
- 🐳 **Docker** — Déploiement containerisé clé en main
- 🧪 **Tests complets** — Unitaires, intégration et Testcontainers

---

## 🛠️ Stack technique

| Couche | Technologie |
|---|---|
| Runtime | Java 21 |
| API | JAX-RS + Jersey + Grizzly |
| Injection | HK2 (CDI) |
| Sécurité | JWT (jjwt) + BCrypt |
| Base de données | MongoDB + Morphia ODM |
| Build | Maven |
| Tests | JUnit 5 + Mockito + Testcontainers |
| Conteneurisation | Docker + Docker Compose |

---

## 🚀 Démarrage rapide

### Prérequis

- Java 21+
- Maven 3.9+
- Docker Desktop

### Lancer avec Docker

```bash
# Cloner le projet
git clone https://github.com/TON-USERNAME/coroute-api.git
cd coroute-api

# Builder et lancer
mvn package -DskipTests
docker-compose up -d

# Vérifier que tout tourne
docker ps
```

L'API est disponible sur **http://localhost:8080**

Mongo Express (interface BD) sur **http://localhost:8081**

### Lancer en local (développement)

```bash
# Démarrer MongoDB seulement
docker-compose up -d mongo

# Lancer l'API
mvn compile exec:java
```

---

## 📡 Endpoints

### Authentification

```
POST /utilisateurs/inscription    Créer un compte
POST /utilisateurs/connexion      Se connecter → retourne un JWT
```

### Trajets

```
GET    /trajets                        Lister tous les trajets (public)
GET    /trajets?depart=Quebec          Filtrer par départ
GET    /trajets?destination=Montreal   Filtrer par destination
GET    /trajets?date=2026-04-15        Filtrer par date
GET    /trajets/{id}                   Détail d'un trajet (public)
POST   /trajets                        Créer un trajet 🔐
DELETE /trajets/{id}                   Supprimer un trajet 🔐
```

### Réservations

```
POST   /trajets/{id}/reservations       Réserver une place 🔐
GET    /trajets/{id}/reservations       Voir les réservations 🔐
DELETE /trajets/{id}/reservations/{rid} Annuler une réservation 🔐
```

> 🔐 = Requiert le header `Authorization: Bearer <token>`

---

## 🔑 Exemples d'utilisation

### 1. Inscription

```bash
curl -X POST http://localhost:8080/utilisateurs/inscription \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Marc Tremblay",
    "email": "marc@coroute.ca",
    "motDePasse": "password123"
  }'
```

### 2. Connexion

```bash
curl -X POST http://localhost:8080/utilisateurs/connexion \
  -H "Content-Type: application/json" \
  -d '{
    "email": "marc@coroute.ca",
    "motDePasse": "password123"
  }'

# Réponse : { "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### 3. Créer un trajet

```bash
curl -X POST http://localhost:8080/trajets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "depart": "Quebec",
    "destination": "Montreal",
    "date": "2026-04-15",
    "heure": "08:30:00",
    "placesDisponibles": 3,
    "prixParPassager": 20.0
  }'
```

### 4. Lister les trajets

```bash
curl http://localhost:8080/trajets
```

---

## 🏗️ Architecture

```
ca.ulaval.coroute/
├── api/
│   ├── controller/       ← Endpoints JAX-RS
│   └── mapper/           ← Gestion des exceptions HTTP
├── config/               ← ApplicationConfig, JWT, MongoDB
├── domain/
│   ├── model/            ← Entités métier (Trajet, Réservation, Utilisateur)
│   ├── service/          ← Logique d'affaires
│   └── exception/        ← Exceptions métier
├── repository/           ← Accès aux données (Mongo + InMemory)
└── dto/
    ├── request/          ← Objets entrants
    └── response/         ← Objets sortants
```

---

## 🧪 Tests

```bash
# Tous les tests
mvn test

# Tests unitaires seulement
mvn test -Dtest="*Test"

# Tests d'intégration seulement
mvn test -Dtest="*IT"

# Tests MongoDB (Testcontainers — Docker requis)
mvn test -Dtest="InMongo*"
```

### Couverture

| Type | Classes testées |
|---|---|
| Unitaires | Services, Mappers, Modèles, Repositories |
| Intégration | Tous les endpoints HTTP |
| Testcontainers | Repositories MongoDB réels |

---

## 🐳 Docker Compose

```yaml
services:
  api:            # API Java sur :8080
  mongo:          # MongoDB sur :27017
  mongo-express:  # Interface web sur :8081
```

### Variables d'environnement

| Variable | Défaut | Description |
|---|---|---|
| `MONGO_URI` | `mongodb://admin:password@mongo:27017/` | URI de connexion MongoDB |
| `MONGO_DB` | `coroute` | Nom de la base de données |

---

## 📁 Structure du projet

```
coroute-api/
├── src/
│   ├── main/java/        ← Code source
│   └── test/java/        ← Tests
├── .github/workflows/    ← CI/CD GitHub Actions
├── Dockerfile            ← Image Docker de l'API
├── docker-compose.yml    ← Stack complète
├── pom.xml               ← Dépendances Maven
└── README.md
```

---

## 👤 Auteur

Développé dans le cadre d'un projet personnel combinant Java, architecture REST et finance quantitative.

**Université Laval** — Programme de finance quantitative

---

## 📄 Licence

MIT License — voir [LICENSE](LICENSE)