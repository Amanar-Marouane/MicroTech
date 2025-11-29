### MicroTech Backend

This repository contains a Spring Boot REST API for SmartShop, a commercial management backend for MicroTech Maroc. It exposes endpoints to manage Clients, Produits, and Commandes with business rules such as loyalty tiers, promo codes, stock validation, and multi-payment handling.

Key characteristics:
- REST only
- Session-based auth planned (no JWT, no Spring Security), controller protection not yet imlemented here
- Layered architecture with DTOs, Mappers (MapStruct), Services, and Repositories
- Centralized exception handling with structured JSON errors
- Pagination model uses start (offset) and size

---

### Build & Run

Prerequisites:
- Java 17+
- Maven 3.9+

Steps:
1. mvn clean package
2. mvn spring-boot:run
3. API base path: http://localhost:8080/api

HTTP examples are provided under http/*.http for use with IntelliJ HTTP Client or similar.

---

### Modules Overview

1) Clients
- CRUD operations exposed in ClientController
- Loyalty stats maintained on order confirmation

2) Produits
- CRUD + search with filters via specification
- Soft delete supported (via @SQLDelete + @SQLRestriction on entity)
- Endpoints in ProductController

3) Commandes
- Create orders (multi-products) with stock validation at creation time
- Promo code handling (see below)
- Automatic price calculation:
  - sousTotalHT (sum of line totals)
  - loyalty discount (tier-based)
  - promo discount (if valid)
  - montantHTApresRemise = sousTotalHT − remises
  - TVA (default 20%)
  - totalTTC
- Payments: add payment, update payment status, confirm (after full payment), cancel (if pending)
- Endpoints in OrderController

---

### Architecture & Contracts

Layers:
- controller: REST endpoints only
- service: business logic
- contract: service interfaces
- mapper: MapStruct mappers between DTOs and entities
- repository: Spring Data JPA repositories
---

### Endpoints

Base prefix: /api (see http/*.http)

Clients (ClientController)
- POST /clients — create
- GET /clients — list
- GET /clients/{id} — get by id
- PUT /clients/{id} — update
- DELETE /clients/{id} — delete

Produits (ProductController)
- POST /produits — create
- GET /produits/{id} — get by id
- GET /produits?start=&size=&nom=&prixMin=&prixMax=&stockMin=&stockMax= — search (pagination + filters)
- PUT /produits/{id} — update
- DELETE /produits/{id} — delete (soft)

Commandes (OrderController)
- POST /commandes/clients/{clientId} — create order (multi-products)
- GET /commandes/{id} — get by id
- GET /commandes?start=&size=&clientId=&statut=&dateFrom=&dateTo=&totalMin=&totalMax=&promoCode= — search
- POST /commandes/{id}/payments — add payment
- POST /commandes/payments/{paiementId}/status — update payment status
- POST /commandes/{id}/confirm — confirm order (requires montantRestant = 0)
- POST /commandes/{id}/cancel — cancel (if pending)

Pagination wrapper: PageResponse<T> with content, totals, start, size, page, etc.

---

### Business Rules (Highlights)

Loyalty tiers (Client.niveauFidelite): BASIC, SILVER, GOLD, PLATINUM
- Updated after each confirmed order based on counts and total spent

Loyalty discounts (applied on sousTotalHT):
- SILVER: 5% if sous-total ≥ 500
- GOLD: 10% if sous-total ≥ 800
- PLATINUM: 15% if sous-total ≥ 1200

Promo codes:
- Optional; when provided must match PROMO-[A-Z0-9]{4}
- Applies a flat 5% discount on sousTotalHT
- Combined with loyalty discount
- Stored in Commande.codePromo and filterable via promoCode

Payments:
- Multi-payments allowed with types: ESPECES, CHEQUE, VIREMENT
- Cash (ESPECES) limited to 20,000 DH per payment
- Only ENCAISSE payments reduce montantRestant
- Order can be confirmed only when montantRestant == 0

Order statuses:
- PENDING, CONFIRMED, CANCELED, REJECTED
- Auto: REJECTED at creation if stock insufficient for any line
- Manual: CONFIRMED (after fully paid), CANCELED (if pending)

---

### Error Handling

Controller-specific @RestControllerAdvice provide structured JSON errors with fields:
- timestamp, status, error, message, path
Specific mappings:
- 400: validation errors (with field list)
- 404: entity not found
- 422: business rule violation
- 500: internal errors

---

### HTTP Samples

See http/clients.http, http/product.http, http/commande.http for ready-to-run requests.

---

### Notes & Next Steps

- API endpoint protection (roles, sessions) is intentionally deferred as per scope.
- If you want to change promo rules (e.g., pattern PROMO-[0-9]{5} and 10% rate), update PromoService accordingly.
- Add unit tests (JUnit/Mockito) for services as needed.
