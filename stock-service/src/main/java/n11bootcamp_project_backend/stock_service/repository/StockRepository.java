package n11bootcamp_project_backend.stock_service.repository;

import n11bootcamp_project_backend.stock_service.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    // Ürün ID'sine göre stok bul
    Optional<Stock> findByProductId(UUID productId);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :quantity WHERE s.productId = :productId AND s.quantity >= :quantity")
    int decrementStockIfSufficient(@Param("productId") UUID productId, @Param("quantity") Integer quantity);
}