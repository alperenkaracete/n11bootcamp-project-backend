package n11bootcamp_project_backend.product_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.product_service.dto.request.CreateProductRequest;
import n11bootcamp_project_backend.product_service.dto.request.UpdateProductRequest;
import n11bootcamp_project_backend.product_service.dto.response.ProductResponse;
import n11bootcamp_project_backend.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Tüm ürünleri sayfalı getir
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    // Tek ürün getir
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // Kategori bazlı getir
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable String category,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
    }

    // İsme göre ara
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String name,
            Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(name, pageable));
    }

    // Ürün ekle (Admin)
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    // Ürün güncelle (Admin)
    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // Ürün sil (Admin)
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}