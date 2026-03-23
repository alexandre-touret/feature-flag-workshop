# Directives pour les Agents IA et Développeurs (AGENTS.md)

Ce fichier définit les règles architecturales, les conventions de code et les bonnes pratiques à suivre pour toute contribution (humaine ou générée par une IA) sur ce projet.

## 🎯 Contexte du Projet
- **Framework** : Quarkus (Cloud Native, Optimisé pour les conteneurs/GraalVM)
- **Langage** : Java 25
- **Architecture** : Architecture Hexagonale (Ports et Adaptateurs / Clean Architecture)

---

## 🏗 Architecture Hexagonale : Règles Strictes

L'application est divisée en 3 couches distinctes. L'objectif principal est de protéger la logique métier de toute dépendance technique.

### 1. Couche Domaine (Core / Domain)
C'est le cœur de l'application. Elle contient les entités métier, les objets de valeur (Value Objects) et les règles métier.
- **Règle absolue** : **ZÉRO dépendance externe**. Aucun import lié à Quarkus, Jackson, Hibernate, RESTEasy, ou `jakarta.*` (sauf éventuellement des annotations basiques de validation métier si abstraites).
- **Modélisation** : Utiliser massivement les `record` de Java pour les objets immuables.
- **Domaine riche** : La logique métier doit résider dans les entités du domaine, pas dans des services anémiques.
- **Gestion des erreurs** : Modéliser les erreurs métier avec des interfaces scellées (`sealed interface`) plutôt que des exceptions lourdes.

### 2. Couche Application (Ports / Use Cases)
Elle orchestre les cas d'utilisation de l'application.
- **Ports Inbound (Entrants)** : Interfaces définissant les actions que l'on peut demander à l'application (les Use Cases).
- **Ports Outbound (Sortants)** : Interfaces définissant comment l'application communique avec l'extérieur (ex: `UserRepository`, `NotificationGateway`).
- Pas d'injection technique ici, uniquement l'orchestration du domaine via les ports.

### 3. Couche Infrastructure (Adapters)
Contient l'implémentation technique des ports. C'est ici que Quarkus et ses extensions sont utilisés.
- **Adapters Inbound (Driving)** : Contrôleurs REST (RESTEasy Reactive), Listeners Kafka/RabbitMQ, CLI. Ils appellent les Ports Inbound.
- **Adapters Outbound (Driven)** : Implémentations des repositories (Hibernate ORM with Panache), clients REST/gRPC externes.
- **Ségrégation** : Les classes de cette couche ne doivent jamais être appelées par le Domaine ou l'Application.

---

## ☕ Bonnes Pratiques Java 25

En tant qu'agent ou développeur, tu dois exploiter au maximum les fonctionnalités de Java 25 :

1. **Records** : Utilise les `record` pour tous les DTOs (Data Transfer Objects), les requêtes REST, et les objets de valeur du domaine.
2. **Pattern Matching (`switch` et `instanceof`)** : Utilise le pattern matching pour le traitement des interfaces scellées (ex: pattern Result/Either) avec des `switch` exhaustifs sans clause `default`.
3. **Sealed Classes/Interfaces** : Utilise-les pour modéliser des hiérarchies de domaine fermées (ex: les états d'une commande, les types d'erreurs métier).
4. **Virtual Threads (Loom)** : Le code blocant doit être évité. Si une opération I/O synchrone est nécessaire dans un adapteur, annote le point d'entrée Quarkus avec `@RunOnVirtualThread` (pour RESTEasy Reactive).

---

## 🚀 Bonnes Pratiques Quarkus

1. **Injection de Dépendances (CDI)** : 
   - Préfère l'injection par le constructeur.
   - Utilise `@ApplicationScoped` pour les services sans état.
2. **Persistence (Hibernate avec Panache)** :
   - Préfère le pattern **Repository** (ex: `PanacheRepository<Entity>`) plutôt que l'Active Record pour respecter l'architecture hexagonale.
   - Les entités JPA (`@Entity`) appartiennent **exclusivement** à la couche Infrastructure.
3. **Mappers** :
   - Ne **JAMAIS** exposer une entité JPA via une API REST.
   - Ne **JAMAIS** utiliser une entité JPA comme entité de Domaine.
   - Utilise `MapStruct` (ou des mappers explicites) pour transformer : `DTO REST` <-> `Entité de Domaine` <-> `Entité JPA`.

---

## 🧪 Stratégie de Test

1. **Tests du Domaine (Unitaires)** : 
   - Testés avec JUnit 5 / AssertJ, **sans** lancer le contexte Quarkus (pas de `@QuarkusTest`). 
   - Ils doivent être ultra-rapides (< 10ms).
2. **Tests d'Intégration (Adapters)** :
   - Utilise `@QuarkusTest`.
   - Utilise **Testcontainers** (via les services Dev de Quarkus "Dev Services" qui démarrent automatiquement PostgreSQL, Kafka, etc., sans configuration docker explicite dans les tests).
   - Mocking : Utilise `@InjectMock` uniquement pour isoler les adaptateurs sortants lors des tests de bout en bout si l'externalité ne peut être instanciée par Testcontainers.

---

## 🚨 Anti-Patterns Strictement Interdits

- ❌ **Domain Leakage** : Renvoyer une entité JPA (Hibernate) directement dans une réponse REST ou l'utiliser dans la logique de domaine pure.
- ❌ **Smart UI / Smart Controllers** : Placer de la logique métier (calculs, vérifications complexes) directement dans un contrôleur REST. Tout doit être délégué à un Use Case.
- ❌ **Anemic Domain Model** : Créer des entités métier qui n'ont que des getters/setters sans aucune méthode de comportement ou de validation d'invariants.
- ❌ **Dépendances cycliques** : La couche domaine dépend de la couche infra (la flèche de dépendance pointe toujours VERS le domaine).

---
*Fin des directives. En tant qu'assistant IA, confirme que tu as lu et compris ces règles avant de proposer une implémentation.*