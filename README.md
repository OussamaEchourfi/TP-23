# TP 23 — Migration de Eureka vers Consul
### Spring Cloud Consul Discovery · Consul KV · Docker Compose · Spring Cloud Gateway

---

## 📋 Objectifs du TP

- **Comprendre la transition architecturale** d'un registre Eureka (spécifique à l'écosystème Java/Spring Cloud) vers **HashiCorp Consul** (agnostique, distribué et résilient).
- **Remplacer les dépendances et configurations** Eureka par `spring-cloud-starter-consul-discovery` et `spring-cloud-starter-consul-config`.
- **Enregistrer dynamiquement les microservices** (`SERVICE-CLIENT`, `SERVICE-CAR`, `SERVICE-GATEWAY`) auprès de l'agent Consul avec des health checks actifs.
- **Assurer la communication inter-services** via OpenFeign en résolvant les noms logiques depuis Consul.
- **Routage dynamique** avec Spring Cloud Gateway basé sur la découverte Consul.
- **Conteneurisation complète** et orchestration de l'ensemble de la pile avec **Docker & Docker Compose**.

---

## 1. Registre de découverte Consul (Services)

Tous les microservices de l'application s'enregistrent dynamiquement auprès de l'agent Consul. Consul surveille leur état de santé en temps réel grâce aux sondes Spring Boot Actuator (`/actuator/health`).

<p align="center">
  <img src="screen/Screenshot 2026-11-24 162305.png" alt="Consul UI — Les 4 services enregistrés et sains" width="100%">
</p>

> **Observation** : Les 4 services (`consul`, `SERVICE-CAR`, `SERVICE-CLIENT`, `SERVICE-GATEWAY`) sont bien enregistrés avec le statut vert (**Healthy**).

---

## 2. États de santé et Health Checks des instances

Chaque instance enregistrée expose ses sondes HTTP Actuator que Consul interroge périodiquement (toutes les 10 secondes) en plus du check de connectivité interne Serf.

### 2.1 Instance SERVICE-CLIENT (Port 8081)
<p align="center">
  <img src="screen/Screenshot 2026-11-24 162323.png" alt="Consul UI — Instance SERVICE-CLIENT et health check UP" width="100%">
</p>

### 2.2 Instance SERVICE-CAR (Port 8082)
<p align="center">
  <img src="screen/Screenshot 2026-11-24 162338.png" alt="Consul UI — Instance SERVICE-CAR et health check UP" width="100%">
</p>

### 2.3 Instance SERVICE-GATEWAY (Port 8888)
<p align="center">
  <img src="screen/Screenshot 2026-11-24 162404.png" alt="Consul UI — Instance SERVICE-GATEWAY et health check UP" width="100%">
</p>

### 2.4 Vue Nœud et vérifications d'infrastructure (Nodes)
<p align="center">
  <img src="screen/Screenshot 2026-11-24 162426.png" alt="Consul UI — Vue Node et checks de santé consolidés" width="100%">
</p>

---

## 3. Routage dynamique et communication inter-services

Spring Cloud Gateway interroge Consul pour découvrir dynamiquement l'emplacement réseau (IP et port) des microservices sans aucune configuration statique.

### 3.1 Routage vers `SERVICE-CLIENT` (`http://localhost:8888/SERVICE-CLIENT/api/client`)
Requête routée via la Gateway vers l'API de gestion des clients :
<p align="center">
  <img src="screen/Screenshot 2026-11-24 162755.png" alt="Gateway vers SERVICE-CLIENT — Données des clients" width="100%">
</p>

### 3.2 Routage vers `SERVICE-CAR` avec jointure Feign (`http://localhost:8888/SERVICE-CAR/api/car`)
La Gateway route la requête vers `SERVICE-CAR`. Ce dernier effectue ensuite un appel OpenFeign vers `SERVICE-CLIENT` (résolu via Consul) pour enrichir chaque voiture avec les détails complets de son propriétaire :
<p align="center">
  <img src="screen/Screenshot 2026-11-24 162848.png" alt="Gateway vers SERVICE-CAR — Jointure inter-services Feign réussie" width="100%">
</p>

---

## 4. Validation du registre de migration

L'API de migration exposée par la Gateway (`/api/migration/registry`) confirme que le registre actif est bien **Consul** et que tous les microservices cibles y sont répertoriés avec leurs adresses réseau et métadonnées :

<p align="center">
  <img src="screen/Screenshot 2026-11-24 162814.png" alt="API Gateway /api/migration/registry confirmant Consul comme provider" width="100%">
</p>

---

## 🚀 Résumé des commandes pour lancer le projet

```powershell
# Déploiement complet avec Docker Compose
docker-compose up --build

# Consul UI    : http://localhost:8500/ui/dc1/services
# Gateway      : http://localhost:8888
# Clients      : http://localhost:8888/SERVICE-CLIENT/api/client
# Voitures     : http://localhost:8888/SERVICE-CAR/api/car
# Registre API : http://localhost:8888/api/migration/registry
```
