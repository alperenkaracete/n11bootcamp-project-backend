package n11bootcamp_project_backend.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.product_service.dto.request.CreateProductRequest;
import n11bootcamp_project_backend.product_service.dto.request.UpdateProductRequest;
import n11bootcamp_project_backend.product_service.dto.response.ProductResponse;
import n11bootcamp_project_backend.product_service.entity.Product;
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

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toResponse); //map(product -> this.toResponse(product));
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponse(product);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponse);
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .category(request.category())
                .build();

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
        if (request.category() != null) product.setCategory(request.category());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID id) {
        // Ürün var mı kontrol et
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
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
                product.getCategory()
        );
    }
}