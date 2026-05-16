# Order Management

A Spring Boot web application for managing a small online store: customers browse products and place orders, while admins manage the catalog and review incoming orders.

## Live Demo

Try it here: **[order-management-u2e1.onrender.com](https://order-management-u2e1.onrender.com/login)**

## Tech Stack

- **Language / Runtime:** Java 21
- **Framework:** Spring Boot 4.0.x (Spring MVC, Spring Security, Spring Data JPA, Bean Validation)
- **View:** Thymeleaf (server-rendered HTML) + `thymeleaf-extras-springsecurity6`
- **Database:** PostgreSQL 16
- **Tooling:** Maven Wrapper, Lombok, multi-stage Dockerfile

## Architecture

Standard layered Spring MVC app. All code lives under `com.lantranle.order`:

| Package       | Responsibility                                                                                                                    |
|---------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `controller/` | HTTP entry points: `HomeController`, `ShopController`, `RegistrationController`, `AdminProductController`, `AdminOrderController` |
| `service/`    | Business logic: `ProductService`, `OrderService`                                                                                  |
| `repository/` | Spring Data JPA repositories for `User`, `Product`, `Order`                                                                       |
| `entity/`     | JPA entities (`User`, `Product`, `Order`, `OrderItem`) and enums (`UserRole`, `OrderStatus`)                                      |
| `dto/`        | Form and view models (requests, error responses, cart views)                                                                      |
| `cart/`       | Session-scoped shopping cart (`Cart`, `CartService`), exposed to views via `config/CartAdvice`                                    |
| `config/`     | `SecurityConfig` (form login + role-based access), `DataInitializer` (seeds demo data on first run)                               |
| `exception/`  | `GlobalExceptionHandler` for centralized error handling                                                                           |

Templates live in `src/main/resources/templates/`, static assets in `src/main/resources/static/`.

## Main Routes

| Route | Audience | Purpose |
| --- | --- | --- |
| `/shop/products` | Customer | Browse catalog, add to cart, checkout |
| `/admin/products` | Admin | Create / update / delete products |
| `/admin/orders` | Admin | Review submitted orders |

