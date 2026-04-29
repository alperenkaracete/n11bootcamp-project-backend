package n11bootcamp_project_backend.shopping_cart_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    private UUID productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;

    // Ürünün kendi ara toplamını hesaplama sorumluluğu kendine aittir (Single Responsibility)
    @JsonIgnore
    public BigDecimal getSubTotal() {
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}