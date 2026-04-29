package n11bootcamp_project_backend.product_service.service;

import n11bootcamp_project_backend.product_service.dto.request.CreateProductRequest;
import n11bootcamp_project_backend.product_service.dto.request.UpdateProductRequest;
import n11bootcamp_project_backend.product_service.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    // Tüm ürünleri sayfalı getir
    Page<ProductResponse> getAllProducts(Pageable pageable);

    // Tek ürün getir
    ProductResponse getProductById(UUID id);

    // Kategori bazlı getir
    Page<ProductResponse> getProductsByCategory(String category, Pageable pageable);

    // İsme göre ara
    Page<ProductResponse> searchProducts(String name, Pageable pageable);

    // Ürün ekle (Admin)
    ProductResponse createProduct(CreateProductRequest request);

    // Ürün güncelle (Admin)
    ProductResponse updateProduct(UUID id, UpdateProductRequest request);

    // Ürün sil (Admin)
    void deleteProduct(UUID id);
}