# CHANGELOG

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