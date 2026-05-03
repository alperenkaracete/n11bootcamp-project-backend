# n11bootcamp-project-backend
N11 Backend Project

Spring Boot ve React.js ile geliştirilmiş, mikroservis mimarisi tabanlı full stack e-ticaret uygulaması. n11 Talenthub Backend Bootcamp kapsamında bitirme projesi olarak geliştirilmektedir.

## Mimari

Proje, Saga Pattern ile dağıtık işlem yönetimi kullanan 9 mikroservisten oluşmaktadır.

### Servisler

| Servis | Port | Açıklama |
|---|---|---|
| Discovery Server | 8761 | Eureka Service Registry |
| Config Server | 8888 | Merkezi konfigürasyon yönetimi |
| API Gateway | 8080 | Tüm isteklerin tek giriş noktası, JWT doğrulama |
| User Service | 8081 | Kimlik doğrulama, JWT, kullanıcı yönetimi |
| Product Service | 8082 | Ürün katalog yönetimi |
| Shopping Cart Service | 8083 | Redis tabanlı sepet yönetimi, guest cart desteği |
| Stock Service | 8084 | Stok yönetimi |
| Order Service | 8085 | Sipariş yönetimi, Saga orchestration |
| Payment Service | 8086 | Iyzico Sandbox ödeme entegrasyonu |
| Notification Service | 8087 | E-posta bildirimleri |
| Log Service | 8088 | Merkezi loglama |

## Teknoloji Yığını

**Backend**
- Java 21
- Spring Boot 3.5
- Spring Cloud (Eureka, Config Server, Gateway)
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Redis
- RabbitMQ (Saga Pattern, asenkron iletişim)
- Iyzico Sandbox

**Frontend**
- React.js
- Axios
- React Router
- Tailwind CSS

**DevOps**
- Docker
- GitHub Actions (CI/CD)
- AWS (Elastic Beanstalk, RDS)

## Mimari Özellikler

### Saga Pattern (Choreography)
Sipariş oluşturma akışında dağıtık işlem yönetimi RabbitMQ üzerinden choreography tabanlı saga ile sağlanır. Her servis kendi sorumluluğunda olan işlemi yapar ve bir sonraki servisi tetikleyen event yayar.

Order Service     → order.created
Stock Service     → stock.reserved / stock.failed
Payment Service   → payment.success / payment.failed
Order Service     → CONFIRMED / CANCELLED
Notification Svc  → mail bildirimi

### JWT + Redis Token Whitelist
Stateless JWT yapısının güvenlik açıklarını kapatmak için access token'lar Redis'te whitelist olarak tutulur. Logout işleminde token Redis'ten silinir, anında geçersiz hale gelir.

### Guest Cart
Anonim kullanıcı sepetleri Redis'te ayrı bir prefix altında tutulur. Kullanıcı login olduğunda guest cart kullanıcı sepetiyle otomatik birleştirilir.

### Compensating Transactions
Saga akışında herhangi bir adım başarısız olursa, önceki başarılı adımlar geri alınır. Stok yetersizliğinde sipariş iptal edilir, ödeme başarısızlığında sipariş iptal edilir ve stok geri alınır.

## Kurulum

### Gereksinimler
- Java 21
- Maven 3.8+
- PostgreSQL 15+
- Redis
- RabbitMQ
- Docker (opsiyonel)

### Veritabanı

PostgreSQL'de şu veritabanlarını oluşturun:
- `user_db`
- `product_db`
- `stock_db`
- `order_db`
- `payment_db`
- `log_db`

### Docker ile Bağımlı Servisler

```bash
docker run -d --name redis -p 6379:6379 redis:latest
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### Servisleri Çalıştırma

Aşağıdaki sırayla servisleri başlatın:

```bash
1. discovery-server
2. config-server
3. api-gateway
4. user-service
5. product-service
6. stock-service
7. shopping-cart-service
8. order-service
9. payment-service
10. notification-service
11. log-service.
```

Her servis için:
```bash
cd <service-name>
./mvnw spring-boot:run
```

### Konfigürasyon

`config-server/src/main/resources/configs/` altında her servis için ayrı `.properties` dosyası bulunur. Hassas bilgiler (JWT secret, mail şifresi, Iyzico API key) `.gitignore` ile korunur.

## API Dokümantasyonu

Tüm servislerin API dokümantasyonu Swagger UI üzerinden gateway aracılığıyla erişilebilir:

**Gateway Swagger UI:** http://34.13.150.164/swagger-ui/index.html

Sağ üstteki "Select a definition" dropdown'undan ilgili servisi seçerek endpoint'leri görüntüleyebilirsiniz.

> **Not:** Swagger UI üzerinden doğrudan istek atılamaz. İsteklerin gateway üzerinden (`http://34.13.150.164/api/...`) gönderilmesi gerekmektedir. Korumalı endpoint'ler için `Authorization: Bearer <token>` header'ı gereklidir.

## CI/CD Pipeline (Sürekli Entegrasyon ve Dağıtım)

Bu projede **GitHub Actions** kullanılarak tam otomatik bir CI/CD hattı kurulmuştur. Kod reposuna yapılan her *push* işleminde aşağıdaki süreç sırasıyla ve otomatik olarak işler:

**Kullanılan Teknolojiler:** GitHub Actions, Docker Hub, Google Cloud Platform (GCP), Discord Webhooks

### Pipeline Akışı:
1. **Build & Push:** Mikroservisler derlenir, Docker imajları oluşturulur ve Docker Hub'a yüklenir (`alperenkaracete/*`).
2. **Configuration Sync:** Güncel `docker-compose.yml` dosyası SCP ile güvenli bir şekilde GCP sunucusuna kopyalanır.
3. **Deploy to GCP:** SSH üzerinden sunucuya bağlanılarak yeni imajlar çekilir (`pull`) ve sistem kesintisi en aza indirilerek servisler yeniden ayağa kaldırılır.
4. **Discord Notification:** Tüm sürecin durumu (Başarılı/Başarısız) anlık olarak Discord geliştirici kanalına mesaj olarak iletilir.

## Test Kart Bilgileri (Iyzico Sandbox)

Kart Numarası : 5528790000000008
Son Kullanma  : 12/30
CVC           : 123
Kart Sahibi   : John Doe

## Mimari Diyagramları

`docs/diagrams/` klasöründe drawio formatında ve PNG export'ları bulunur:
- `architecture.png` — Sistem mimarisi

## Geliştirici

Alperen Karaçete  
n11 Talenthub Backend Bootcamp  
[GitHub](https://github.com/alperenkaracete)

## Lisans

MIT