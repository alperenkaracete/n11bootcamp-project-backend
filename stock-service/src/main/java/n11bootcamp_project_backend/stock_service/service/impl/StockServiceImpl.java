package n11bootcamp_project_backend.stock_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.producer.LogProducer;
import n11bootcamp_project_backend.stock_service.dto.event.OrderItemMessage;
import n11bootcamp_project_backend.stock_service.dto.request.CreateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.request.UpdateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.response.StockResponse;
import n11bootcamp_project_backend.stock_service.entity.Stock;
import n11bootcamp_project_backend.stock_service.repository.StockRepository;
import n11bootcamp_project_backend.stock_service.service.StockService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final LogProducer logProducer;

    @Override
    public StockResponse createStock(CreateStockRequest request) {
        if (stockRepository.findByProductId(request.productId()).isPresent()) {
            logProducer.sendLog("stock-service", "ERROR", "Stock creation failed: Already exists for product -> " + request.productId());
            throw new RuntimeException("Stock already exists for this product");
        }

        Stock stock = Stock.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .build();

        Stock savedStock = stockRepository.save(stock);

        logProducer.sendLog("stock-service", "INFO", "Initial stock created for product: " + request.productId() + ", Quantity: " + request.quantity());

        return toResponse(savedStock);
    }

    @Override
    public StockResponse getStockByProductId(UUID productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    logProducer.sendLog("stock-service", "ERROR", "Stock fetch failed: Product not found -> " + productId);
                    return new RuntimeException("Stock not found");
                });
        return toResponse(stock);
    }

    @Override
    public StockResponse updateStock(UUID productId, UpdateStockRequest request) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    logProducer.sendLog("stock-service", "ERROR", "Stock update failed: Product not found -> " + productId);
                    return new RuntimeException("Stock not found");
                });

        stock.setQuantity(request.quantity());
        Stock updatedStock = stockRepository.save(stock);

        logProducer.sendLog("stock-service", "INFO", "Stock updated manually for product: " + productId + ", New Quantity: " + request.quantity());

        return toResponse(updatedStock);
    }

    @Override
    public void decreaseStock(UUID productId, Integer quantity) {
        int updatedRows = stockRepository.decrementStockIfSufficient(productId, quantity);

        if (updatedRows == 0) {
            logProducer.sendLog("stock-service", "ERROR", "Stock reduction failed: Insufficient stock or product not found. Product: " + productId + ", Requested: " + quantity);
            throw new RuntimeException("Stock not found or insufficient stock for productId: " + productId);
        }

        logProducer.sendLog("stock-service", "INFO", "Stock decreased for product: " + productId + ", Amount: " + quantity);
    }

    @Override
    @Transactional
    public void decreaseStocksForOrder(List<OrderItemMessage> items) {
        logProducer.sendLog("stock-service", "INFO", "Batch stock reduction started for order. Item count: " + items.size());

        for (OrderItemMessage item : items) {
            decreaseStock(UUID.fromString(item.productId()), item.quantity());
        }

        logProducer.sendLog("stock-service", "INFO", "Batch stock reduction completed successfully.");
    }

    @Override
    public void deleteStock(UUID productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    logProducer.sendLog("stock-service", "ERROR", "Stock deletion failed: Product not found -> " + productId);
                    return new RuntimeException("Stock not found");
                });

        stockRepository.delete(stock);
        logProducer.sendLog("stock-service", "WARN", "Stock record deleted for product: " + productId);
    }

    @Override
    @Transactional
    public void restoreStocksForOrder(List<OrderItemMessage> items) {
        for (OrderItemMessage item : items) {
            Stock stock = stockRepository.findByProductId(UUID.fromString(item.productId()))
                    .orElseThrow(() -> new RuntimeException("Stock not found for productId: " + item.productId()));

            stock.setQuantity(stock.getQuantity() + item.quantity());
            stockRepository.save(stock);
        }
        logProducer.sendLog("stock-service", "WARN", "Stock restored due to payment failure. Item count: " + items.size());
    }

    @Override
    @Transactional
    public void increaseStock(UUID productId, Integer quantity) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found for productId: " + productId));

        stock.setQuantity(stock.getQuantity() + quantity);
        stockRepository.save(stock);
        logProducer.sendLog("stock-service", "INFO", "Stock increased for product: " + productId + ", Amount: " + quantity);
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getProductId(),
                stock.getQuantity()
        );
    }
}