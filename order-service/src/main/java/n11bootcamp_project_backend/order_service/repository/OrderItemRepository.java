package n11bootcamp_project_backend.order_service.repository;

import n11bootcamp_project_backend.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}