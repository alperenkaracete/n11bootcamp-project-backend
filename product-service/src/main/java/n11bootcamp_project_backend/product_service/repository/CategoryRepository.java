package n11bootcamp_project_backend.product_service.repository;

import n11bootcamp_project_backend.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}