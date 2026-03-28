# Système de Suivi des Médailles Olympiques

Application backend de gestion et de suivi des médailles olympiques en temps réel,
développée avec Spring Boot dans le cadre du module Développement Backend à
Polytech Diamniadio (Université Amadou Moctar Mbow) — Année académique 2025-2026.

---

## Table des matières

- [Stack technique](#stack-technique)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Lancer l'application](#lancer-lapplication)
- [API REST](#api-rest)
- [Tests](#tests)

---

## Stack technique

- **Java** 21
- **Spring Boot** 3.5.x
- **Spring Data JPA** + Hibernate
- **MySQL** 8.0
- **Maven** 3.9.x
- **Lombok**
- **JUnit 5** + Mockito + JaCoCo

---

## Prérequis

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Git

---

## Installation

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd olympic-medals
```

### 2. Créer la base de données MySQL

```bash
mysql -u root -p
```

```sql
CREATE DATABASE olympic_medals CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'olympic_user'@'localhost' IDENTIFIED BY 'olympic2026';
GRANT ALL PRIVILEGES ON olympic_medals.* TO 'olympic_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3. Configurer l'application

Copier le fichier exemple et remplir les valeurs :

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Ouvrir `src/main/resources/application.properties` et remplacer :

```properties
spring.datasource.username=VOTRE_USERNAME
spring.datasource.password=VOTRE_PASSWORD
```

---

## Lancer l'application

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

Les tables sont créées automatiquement par Hibernate au premier démarrage.

---

## API REST

### Pays

| Méthode | Endpoint            | Description         |
| ------- | ------------------- | ------------------- |
| GET     | `/api/v1/pays`      | Liste tous les pays |
| GET     | `/api/v1/pays/{id}` | Détails d'un pays   |
| POST    | `/api/v1/pays`      | Créer un pays       |
| PUT     | `/api/v1/pays/{id}` | Modifier un pays    |
| DELETE  | `/api/v1/pays/{id}` | Supprimer un pays   |

### Athlètes

| Méthode | Endpoint                         | Description             |
| ------- | -------------------------------- | ----------------------- |
| GET     | `/api/v1/athletes`               | Liste tous les athlètes |
| GET     | `/api/v1/athletes/{id}`          | Détails d'un athlète    |
| GET     | `/api/v1/athletes/pays/{paysId}` | Athlètes d'un pays      |
| POST    | `/api/v1/athletes`               | Créer un athlète        |
| PUT     | `/api/v1/athletes/{id}`          | Modifier un athlète     |
| DELETE  | `/api/v1/athletes/{id}`          | Supprimer un athlète    |

### Compétitions

| Méthode | Endpoint                    | Description                   |
| ------- | --------------------------- | ----------------------------- |
| GET     | `/api/v1/competitions`      | Liste toutes les compétitions |
| GET     | `/api/v1/competitions/{id}` | Détails d'une compétition     |
| POST    | `/api/v1/competitions`      | Créer une compétition         |
| PUT     | `/api/v1/competitions/{id}` | Modifier une compétition      |
| DELETE  | `/api/v1/competitions/{id}` | Supprimer une compétition     |

### Médailles

| Méthode | Endpoint                                        | Description                 |
| ------- | ----------------------------------------------- | --------------------------- |
| GET     | `/api/v1/medailles`                             | Liste toutes les médailles  |
| GET     | `/api/v1/medailles/{id}`                        | Détails d'une médaille      |
| GET     | `/api/v1/medailles/athlete/{athleteId}`         | Médailles d'un athlète      |
| GET     | `/api/v1/medailles/competition/{competitionId}` | Médailles d'une compétition |
| POST    | `/api/v1/medailles`                             | Enregistrer une médaille    |

### Classement

| Méthode | Endpoint                           | Description                                      |
| ------- | ---------------------------------- | ------------------------------------------------ |
| GET     | `/api/v1/classement`               | Classement par total de médailles                |
| GET     | `/api/v1/classement?tri=or`        | Classement par médailles d'or                    |
| GET     | `/api/v1/classement?tri=points`    | Classement par points (or=3, argent=2, bronze=1) |
| GET     | `/api/v1/classement/pays/{paysId}` | Statistiques d'un pays                           |

### Format des réponses

Toutes les réponses suivent ce format uniforme :

```json
{
  "success": true,
  "message": "Message descriptif",
  "data": { ... },
  "timestamp": "2026-03-27T10:30:00"
}
```

### Codes de statut HTTP

| Code | Signification         |
| ---- | --------------------- |
| 200  | Succès                |
| 201  | Ressource créée       |
| 204  | Suppression réussie   |
| 400  | Données invalides     |
| 404  | Ressource introuvable |
| 409  | Ressource dupliquée   |
| 500  | Erreur serveur        |

### Exemple — Créer un pays

```bash
curl -X POST http://localhost:8080/api/v1/pays \
  -H "Content-Type: application/json" \
  -d '{"code": "SEN", "nom": "Sénégal", "drapeau": "🇸🇳"}'
```

Réponse :

```json
{
  "success": true,
  "message": "Pays créé avec succès",
  "data": {
    "id": 1,
    "code": "SEN",
    "nom": "Sénégal",
    "drapeau": "🇸🇳"
  },
  "timestamp": "2026-03-27T10:30:00"
}
```

---

## Sécurité

### Rate Limiting

L'API est protégée contre les abus par un système de rate limiting basé sur
l'algorithme du token bucket (Bucket4j).

**Limite :** 100 requêtes par minute par adresse IP.

Au-delà de cette limite, l'API retourne une erreur **429 Too Many Requests** :

```json
{
  "success": false,
  "message": "Trop de requêtes. Veuillez réessayer dans une minute.",
  "timestamp": "2026-03-28T10:30:00"
}
```

Le header `X-RateLimit-Remaining` est inclus dans chaque réponse pour indiquer
le nombre de requêtes restantes dans la fenêtre courante.

---

## Tests

### Lancer les tests

```bash
mvn test
```

### Rapport de couverture

```bash
mvn test
xdg-open target/site/jacoco/index.html
```

Couverture actuelle : **96%**

### Structure des tests

```
src/test/
├── java/com/polytech/olympic_medals/
│   ├── service/
│   │   ├── PaysServiceImplTest.java
│   │   ├── AthleteServiceImplTest.java
│   │   ├── CompetitionServiceImplTest.java
│   │   └── MedailleServiceImplTest.java
│   └── controller/
│       ├── PaysControllerIntegrationTest.java
│       ├── AthleteControllerIntegrationTest.java
│       ├── CompetitionControllerIntegrationTest.java
│       ├── MedailleControllerIntegrationTest.java
│       └── ClassementControllerIntegrationTest.java
└── resources/
    └── application.properties
```

---
