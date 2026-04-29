package n11bootcamp_project_backend.stock_service.repository;

import n11bootcamp_project_backend.stock_service.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    // Ürün ID'sine göre stok bul
    Optional<Stock> findByProductId(UUID productId);
}