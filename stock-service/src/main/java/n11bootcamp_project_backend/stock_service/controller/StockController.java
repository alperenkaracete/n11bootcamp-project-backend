package n11bootcamp_project_backend.stock_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.stock_service.dto.request.CreateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.request.UpdateStockRequest;
import n11bootcamp_project_backend.stock_service.dto.response.StockResponse;
import n11bootcamp_project_backend.stock_service.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // Stok ekle (Admin)
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponse> createStock(
            @Valid @RequestBody CreateStockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockService.createStock(request));
    }

    // Ürün ID'sine göre stok getir
    @GetMapping("/{productId}")
    public ResponseEntity<StockResponse> getStockByProductId(
            @PathVariable UUID productId) {
        return ResponseEntity.ok(stockService.getStockByProductId(productId));
    }

    // Stok güncelle (Admin)
    @PutMapping("/{productId}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponse> updateStock(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(stockService.updateStock(productId, request));
    }

    // Stok sil (Admin)
    @DeleteMapping("/{productId}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStock(@PathVariable UUID productId) {
        stockService.deleteStock(productId);
        return ResponseEntity.noContent().build();
    }
}