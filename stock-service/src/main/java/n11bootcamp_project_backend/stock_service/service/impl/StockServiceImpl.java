package n11bootcamp_project_backend.stock_service.service.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.stock_service.dto.request.CreateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.request.UpdateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.response.StockResponse;
import n11bootcamp_project_backend.stock_service.entity.Stock;
import n11bootcamp_project_backend.stock_service.repository.StockRepository;
import n11bootcamp_project_backend.stock_service.service.StockService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public StockResponse createStock(CreateStockRequest request) {
        // Aynı ürün için stok zaten var mı?
        if (stockRepository.findByProductId(request.productId()).isPresent()) {
            throw new RuntimeException("Stock already exists for this product");
        }

        Stock stock = Stock.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .build();

        return toResponse(stockRepository.save(stock));
    }

    @Override
    public StockResponse getStockByProductId(UUID productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        return toResponse(stock);
    }

    @Override
    public StockResponse updateStock(UUID productId, UpdateStockRequest request) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        stock.setQuantity(request.quantity());
        return toResponse(stockRepository.save(stock));
    }

    @Override
    public void decreaseStock(UUID productId, Integer quantity) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        // Yeterli stok var mı?
        if (stock.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);
    }

    @Override
    public void deleteStock(UUID productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        stockRepository.delete(stock);
    }

    // Entity → Response dönüşümü
    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getProductId(),
                stock.getQuantity()
        );
    }
}