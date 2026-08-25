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

All endpoints are under `/api` and exchange JSON. There's no auth — see
[Known limitations](#known-limitations).

### Customers

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/customers` | Create a customer. Body: `name`, `address`, `phone` (required), `email` (optional, validated). Returns `201` with the created customer. |
| `GET` | `/customers` | List every customer. |
| `GET` | `/customers/{id}` | Get one customer by ID, or `404` if it doesn't exist. |
| `GET` | `/customers/by-phone?phone=` | Look up customers by exact phone match (digits only, e.g. `6512297136`). Returns a list — empty if no match. |
| `PUT` | `/customers/{id}` | Replace a customer's name/address/phone/email. |
| `DELETE` | `/customers/{id}` | Delete a customer, cascading to their recipients and shipments. |

A customer response looks like: `{ id, name, address, phone, email }`.

### Recipients

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/recipients` | Create a recipient under a customer. Body: `name`, `address`, `phone`, `customerId` (all required). `404`s if `customerId` doesn't exist. |
| `GET` | `/recipients` | List every recipient. |
| `GET` | `/recipients/{id}` | Get one recipient by ID. |
| `GET` | `/recipients/by-customer/{customerId}` | List every recipient that belongs to a given customer. |
| `PUT` | `/recipients/{id}` | Update a recipient's name/address/phone (their `customerId` can't be changed this way). |
| `DELETE` | `/recipients/{id}` | Delete a recipient. |

A recipient response looks like: `{ id, name, address, phone, customerId }`.

### Shipments

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/shipments` | Create a shipment. Body: `customerId`, `recipientId`, `description`, `zone` (`CITY` or `OTHER`), `declaredValue` (optional), `boxes` (at least one, each `{ label?, weight }`), `packageItems` (optional, each `{ itemName, pricingType: PER_POUND \| PER_EACH, quantity, rate }`). Pricing and `totalCost` are computed server-side — see below. |
| `GET` | `/shipments` | List every shipment as a lightweight summary (`id`, `recipientName`, `description`, `zone`, `status`, `totalCost`, `createdDate`) — no boxes/items. |
| `GET` | `/shipments/{id}` | Get one shipment's full detail: nested `customer`, `recipient`, all `boxes`, all `packageItems`, `status`, `trackingNumber`, `totalCost`, `createdDate`. |
| `GET` | `/shipments/by-customer-phone?phone=` | List shipment summaries for a customer, matched by phone (substring match, not exact). |
| `GET` | `/shipments/by-status/{status}` | List shipment summaries filtered by status (`PENDING` or `DELIVERED`). |
| `PATCH` | `/shipments/{id}/tracking-number` | Set the tracking number. Body: `{ trackingNumber }`. |
| `PATCH` | `/shipments/{id}/mark-delivered` | Flip status to `DELIVERED`. No body. |
| `DELETE` | `/shipments/{id}` | Delete a shipment, cascading to its boxes and package items. |

**Pricing** is calculated automatically on creation, never sent by the
client: each box is charged by weight ($4.50/lb in the `CITY` zone,
$5.00/lb otherwise), and each package item is charged `quantity × rate`
(per pound or per item, depending on its `pricingType`). `totalCost` is
the sum of both.

Every `404`/validation error returns a JSON body like
`{ status, error, message, timestamp }` (or, for validation failures, a
`{ field: message }` map) via a global exception handler.

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
