# Order Management

Spring Boot order-management demo for product administration, shop checkout, and admin order review.

## Requirements

- Java 21
- Docker and Docker Compose, for local PostgreSQL
- Maven wrapper included in the project

## Setup

Copy the sample environment file and adjust values if needed:

```bash
cp .env.example .env
```

Start PostgreSQL:

```bash
docker compose up -d
```

Run the application:

```bash
sh mvnw spring-boot:run
```

Open `http://localhost:8080`.

## Demo Accounts

The demo data initializer is enabled by default with `APP_DEMO_ENABLED=true`.

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | value of `DEMO_ADMIN_PASSWORD` in `.env` |
| User | `user` | value of `DEMO_USER_PASSWORD` in `.env` |

To run without demo seed data, set:

```properties
APP_DEMO_ENABLED=false
```

## Main Flows

- Admin product management: `/admin/products`
- User product listing and checkout: `/shop/products`
- Admin order viewing: `/admin/orders`

## Tests

```bash
sh mvnw test
```
