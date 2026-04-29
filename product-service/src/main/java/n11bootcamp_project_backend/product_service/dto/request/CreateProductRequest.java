package n11bootcamp_project_backend.product_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Image URL cannot be blank")
        String imageUrl,

        @NotBlank(message = "Category cannot be blank")
        String category
) {}