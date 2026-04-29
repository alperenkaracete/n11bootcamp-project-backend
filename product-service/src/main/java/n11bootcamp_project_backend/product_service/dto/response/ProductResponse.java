package n11bootcamp_project_backend.product_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(

        UUID id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String category
) {}