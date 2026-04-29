package n11bootcamp_project_backend.stock_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Hangi ürünün stoğu
    @Column(nullable = false, unique = true)
    private UUID productId;

    // Stok miktarı
    @Column(nullable = false)
    private Integer quantity;
}