package n11bootcamp_project_backend.shopping_cart_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    private UUID userId;
    private List<CartItem> items = new ArrayList<>();

    // Toplam fiyat hesapla

    @JsonIgnore
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getSubTotal) // Ürünleri ara toplama çevir
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Hepsini topla
    }
}