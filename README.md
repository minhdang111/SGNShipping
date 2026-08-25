# SGN Shipping

A full-stack shipping management app: create customers and recipients, build
shipments with per-box weight and per-item pricing, and look up past
shipments by ID or by customer. Built with a Spring Boot REST API and a
React frontend, containerized end-to-end.

## Features

- **Customers & recipients** — create customers, look them up by phone
  number, and manage the recipients tied to each one.
- **Shipment creation** — pick a sender and recipient, add one or more
  boxes (weight-based pricing) and optional package items (per-pound or
  per-item pricing), and the total cost is calculated automatically.
- **Search by shipment ID** — full detail view: sender, recipient, boxes,
  items, status, tracking number, and total cost.
- **Search by customer** — look a customer up by phone and browse every
  shipment they've sent, then drill into any one for full details.

## Tech stack

**Backend** — Java 21, Spring Boot 3, Spring Data JPA, Spring Validation,
PostgreSQL (H2 for tests), Gradle, JUnit 5 + Mockito.

**Frontend** — React 19, React Router, Axios, Vite.

**Infra** — Docker Compose (Postgres + Spring Boot + an nginx-served static
build), all wired to come up together with one command.

## Running it

### Docker Compose (one command)

```bash
cp .env.example .env   # fill in a real DB_USERNAME / DB_PASSWORD
docker compose up --build
```

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api
- Postgres: localhost:5432

### Running the pieces individually (for development)

Backend (needs Postgres running — `docker compose up -d postgres` works,
or point `DB_URL` at your own instance):

```bash
cd backend
DB_USERNAME=... DB_PASSWORD=... ./gradlew bootRun
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

## API overview

| Resource | Endpoints |
|---|---|
| Customers | `POST /api/customers`, `GET /api/customers`, `GET /api/customers/{id}`, `GET /api/customers/by-phone?phone=`, `PUT /api/customers/{id}`, `DELETE /api/customers/{id}` |
| Recipients | `POST /api/recipients`, `GET /api/recipients`, `GET /api/recipients/{id}`, `GET /api/recipients/by-customer/{customerId}`, `PUT /api/recipients/{id}`, `DELETE /api/recipients/{id}` |
| Shipments | `POST /api/shipments`, `GET /api/shipments`, `GET /api/shipments/{id}`, `GET /api/shipments/by-customer-phone?phone=`, `GET /api/shipments/by-status/{status}`, `PATCH /api/shipments/{id}/tracking-number`, `PATCH /api/shipments/{id}/mark-delivered`, `DELETE /api/shipments/{id}` |

Pricing: each box is charged by weight ($4.50/lb within the city zone,
$5.00/lb otherwise); package items are charged per pound or per item,
whichever pricing type is set. The shipment's total cost is the sum of
both.

## Project structure

```
backend/    Spring Boot API (entity / repository / service / controller / dto layers)
frontend/   React app (pages / components / api client)
```

## Known limitations

This is a portfolio project, not a production system — a few things are
intentionally out of scope:

- No authentication or authorization; every endpoint is open.
- Schema is managed via Hibernate's `ddl-auto=update`, not a migration
  tool like Flyway — fine for a demo, not for a real production database.
- No CI pipeline.
