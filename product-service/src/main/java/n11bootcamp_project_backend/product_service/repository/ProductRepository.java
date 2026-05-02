package n11bootcamp_project_backend.product_service.repository;

import n11bootcamp_project_backend.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Kategori bazlı filtreleme
    // ProductRepository.java
    Page<Product> findByCategoryName(String name, Pageable pageable);

    // İsme göre arama
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}