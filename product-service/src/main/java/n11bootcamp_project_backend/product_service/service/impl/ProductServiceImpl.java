package n11bootcamp_project_backend.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.producer.LogProducer;
import n11bootcamp_project_backend.product_service.dto.request.CreateProductRequest;
import n11bootcamp_project_backend.product_service.dto.request.UpdateProductRequest;
import n11bootcamp_project_backend.product_service.dto.response.ProductResponse;
import n11bootcamp_project_backend.product_service.entity.Category;
import n11bootcamp_project_backend.product_service.entity.Product;
import n11bootcamp_project_backend.product_service.repository.CategoryRepository;
import n11bootcamp_project_backend.product_service.repository.ProductRepository;
import n11bootcamp_project_backend.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final LogProducer logProducer;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toResponse); //map(product -> this.toResponse(product));
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logProducer.sendLog("product-service", "ERROR", "Product could not found with id: " + id);
                    return new RuntimeException("Product could not found");
                });
        return toResponse(product);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryName(category, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponse);
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        // String olan kategori isminden Category nesnesini bul
        Category category = categoryRepository.findByName(request.category()).orElseThrow(() -> {
                logProducer.sendLog("product-service", "ERROR", "Category could not found.");
                return new RuntimeException("Category could not found");
                });

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .category(category)
                .build();

        logProducer.sendLog("product-service", "INFO", "Product: " + request.name() + "created successfully.");
        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sadece null olmayan alanları güncelle
        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.price() != null) product.setPrice(request.price());
        if (request.imageUrl() != null) product.setImageUrl(request.imageUrl());

        if (request.category() != null) {
            Category category = categoryRepository.findByName(request.category())
                    .orElseThrow(() -> {
                        logProducer.sendLog("product-service", "ERROR", "Product: " + request.name() + "could not be updated.");
                        return new RuntimeException("Category not found");
                    });
            product.setCategory(category); // String yerine Category nesnesini verdik
        }
        logProducer.sendLog("product-service", "INFO", "Product: " + request.name() + "updated successfully.");
        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID id) {
        // Ürün var mı kontrol et
        if (!productRepository.existsById(id)) {
            logProducer.sendLog("product-service", "ERROR", "Product with id: " + id + "could not be found.");
            throw new RuntimeException("Product could not found");
        }
        logProducer.sendLog("product-service", "INFO", "Product with id: " + id + "deleted successfully.");
        productRepository.deleteById(id);
    }

    // Entity → Response dönüşümü
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory().getName()
        );
    }
}