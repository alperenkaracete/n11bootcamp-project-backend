# CHANGELOG

## [0.8.0] - 2026-04-30

### Added
- Log Service implemented (port 8088)
  - RabbitMQ consumer for log events
  - Log storage in PostgreSQL
  - Log endpoints by service and level

## [0.7.0] - 2026-04-30

### Added
- Notification Service implemented (port 8087)
  - RabbitMQ consumer for payment.success event
  - Email notification on successful payment
  - Welcome email on user registration

## [0.6.0] - 2026-04-30

### Added
- Payment Service implemented (port 8086)
  - Iyzico Sandbox integration
  - Payment processing with success/failed status
  - Duplicate payment prevention for successful payments
  - RabbitMQ event publish (payment.success, payment.failed)
  - Swagger documentation enabled

## [0.5.0] - 2026-04-30

### Added
- Order Service implemented (port 8085)
  - Order and OrderItem entities with OneToMany relation
  - Order CRUD operations
  - OrderStatus enum (PENDING, CONFIRMED, CANCELLED)
  - RabbitMQ event publish on order creation (queue config pending)
  - Swagger documentation enabled

## [0.4.0] - 2026-04-30

### Added
- Shopping Cart Service implemented (port 8083)
  - Cart CRUD operations
  - Redis based cart storage
  - Guest cart merge on login
  - Swagger documentation enabled

## [0.3.0] - 2026-04-29

### Added
- Stock Service implemented (port 8084)
  - Stock CRUD operations
  - Stock decrease method (RabbitMQ integration pending)
  - Swagger documentation enabled

## [0.2.0] - 2026-04-29

### Added
- Product Service implemented (port 8082)
  - Product CRUD operations
  - Pagination and search by name
  - Category filtering
  - Swagger documentation enabled

## [0.1.0] - 2026-04-28

### Added
- User Service implemented (port 8081)
    - JWT authentication (access token, refresh token)
    - Redis token whitelist for instant logout
    - Spring Security configured (stateless, JWT filter)
    - Swagger documentation enabled
- Architecture diagram added (docs/diagrams/)

## [0.0.1] - 2026-04-27

### Added
- Discovery Server (Eureka) added and configured - port 8761
- Config Server added and configured - port 8888
- API Gateway (Reactive) added and configured - port 8080
- Mono-repo structure initialized
- .gitignore created