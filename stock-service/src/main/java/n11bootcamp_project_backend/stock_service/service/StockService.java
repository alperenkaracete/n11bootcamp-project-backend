package n11bootcamp_project_backend.stock_service.service;

import n11bootcamp_project_backend.stock_service.dto.request.CreateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.request.UpdateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.response.StockResponse;

import java.util.UUID;

public interface StockService {

    // Stok ekle
    StockResponse createStock(CreateStockRequest request);

    // Ürün ID'sine göre stok getir
    StockResponse getStockByProductId(UUID productId);

    // Stok güncelle (Admin)
    StockResponse updateStock(UUID productId, UpdateStockRequest request);

    // Stok düş (RabbitMQ'dan tetiklenecek)
    void decreaseStock(UUID productId, Integer quantity);

    // Stok sil
    void deleteStock(UUID productId);
}