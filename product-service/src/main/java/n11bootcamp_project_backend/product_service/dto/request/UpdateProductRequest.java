package n11bootcamp_project_backend.product_service.dto.request;

import java.math.BigDecimal;

public record UpdateProductRequest(

        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String category
) {}