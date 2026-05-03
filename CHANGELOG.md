# CHANGELOG

## [1.0.1] - 2026-05-03 - Dockerization & DevOps Integration

### Added
- Integrated **Google Jib Maven Plugin** across all microservices for automated containerization.
- Configured container metadata including main classes, ports, and base images.
- Set up **Java 21 (Eclipse Temurin)** as the lightweight Alpine-based runtime environment.
- Established connection parameters for the **Docker Hub** repository (`alperenkaracete`).
- Removed the need for manual Dockerfile maintenance, streamlining the build process.

## [1.0.0] - 2026-05-03

### Added
- stock-service: PaymentFailedConsumer for stock compensation on payment failure
- stock-service: OrderServiceClient (FeignClient) to retrieve order items
- stock-service: increaseStock method for stock rollback
- stock-service: StockSyncController for manual stock management
- payment-service: PaymentEventConsumer, RedisConfig
- order-service: GET /{orderId}/items endpoint for stock compensation queries
- order-service: PaymentRequest DTO, RedisConfig
- product-service: Category entity, CategoryRepository, DummyDataSeeder

### Changed
- All services: integrated LogProducer for centralized logging via common-lib
- All services: removed duplicate RabbitMQ bean definitions, using CommonRabbitConfig
- All services: added scanBasePackages for common-lib bean discovery
- stock-service: enabled @EnableFeignClients, updated SecurityConfig and RabbitMQConfig
- user-service: fixed misplaced log statements in AuthServiceImpl

### Fixed
- Stock not being restored after payment failure (saga compensation flow)

## [0.11.0] - 2026-05-03

### Added
- common-lib module: shared DTOs (LogMessage, OrderItemQuantityResponse), RabbitConstants, CommonRabbitConfig, LogProducer
- Centralized log publishing via RabbitMQ across all services
- log-service: exchange/queue/binding config with RabbitConstants
- notification-service: refactored consumers (PaymentNotificationConsumer, WelcomeNotificationConsumer)

### Changed
- log-service, notification-service: scanBasePackages added for common-lib bean scanning
- RabbitMQ JSON converter moved to common-lib, removed duplicates from individual services
- log-service.properties updated in config-server

### Removed
- Duplicate RabbitMQ config (jsonMessageConverter, rabbitTemplate) from individual services
- notification-service: old NotificationConsumer and WelcomeConsumer replaced with renamed versions

## [0.10.0] - 2026-05-03

### Added
- API Gateway with JWT + Redis auth, route config and logging filter implemented
  - Spring Cloud Gateway (WebMVC) route definitions via RouteConfig
  - AuthenticationFilter with JWT validation and Redis token check
  - X-User-Id, X-User-Role headers forwarded to downstream services
  - Public/protected route separation
  - CORS configuration for React frontend

## [0.9.0] - 2026-05-01

### Added
- Full saga pattern integration completed
  - Order → Stock → Payment → Confirmed flow
  - Order status updates via RabbitMQ events
  - Payment failed → Order cancelled
  - Order events include product items
  - Automatic stock decrease on order
  - Order cancellation on stock failure
  - JSON message conversion across services

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