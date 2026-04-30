package n11bootcamp_project_backend.order_service.repository;

import n11bootcamp_project_backend.order_service.entity.Order;
import n11bootcamp_project_backend.order_service.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Kullanıcının tüm siparişlerini getir
    List<Order> findByUserId(UUID userId);

    // Kullanıcının belirli durumdaki siparişleri
    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);
}