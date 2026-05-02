package n11bootcamp_project_backend.stock_service.controller; // Kendi paket adına göre düzelt

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.stock_service.entity.Stock;
import n11bootcamp_project_backend.stock_service.repository.StockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Slf4j
public class StockSyncController {

    private final StockRepository stockRepository;

    // HTTP istekleri atmak için RestTemplate kullanıyoruz
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/sync-dummy-data")
    public ResponseEntity<String> syncFromProductService() {

        // ⚠️ DİKKAT: Buraya Product Service'inin çalıştığı portu ve ürünleri listelediğin endpointi yazmalısın.
        // Örnek: Eğer Product Service 8081'de çalışıyorsa:
        String productServiceUrl = "http://localhost:8081/api/products";

        try {
            log.info("Product Service'ten ürünler çekiliyor: {}", productServiceUrl);

            // Product Service'e GET isteği atıyoruz.
            // DTO (Class) kirliliği olmasın diye veriyi saf JSON (JsonNode) olarak alıyoruz. (Loose Coupling!)
            JsonNode response = restTemplate.getForObject(productServiceUrl, JsonNode.class);

            if (response == null || !response.isArray()) {
                // Eğer senin Product Service'in veriyi direkt liste değil de {"data": [...]} gibi bir objede dönüyorsa
                // response = response.get("data"); şeklinde bir düzeltme yapman gerekebilir.
                return ResponseEntity.badRequest().body("Ürünler okunamadı. URL veya JSON formatını kontrol et.");
            }

            List<Stock> newStocks = new ArrayList<>();

            // Gelen JSON listesindeki her bir ürünün üzerinde dönüyoruz
            for (JsonNode node : response) {
                // Sadece "id" alanını okumamız yeterli
                String productIdStr = node.get("id").asText();
                UUID productId = UUID.fromString(productIdStr);

                // Bu ürün için Stock veritabanında kayıt yoksa ekle
                if (stockRepository.findByProductId(productId).isEmpty()) {
                    Stock stock = Stock.builder()
                            .productId(productId)
                            .quantity(100) // Hepsine 100 stok veriyoruz
                            .build();
                    newStocks.add(stock);
                }
            }

            if (newStocks.isEmpty()) {
                return ResponseEntity.ok("Tüm ürünlerin stoku zaten mevcut, eklenecek yeni ürün bulunamadı.");
            }

            // Toplu halde veritabanına kaydet
            stockRepository.saveAll(newStocks);
            log.info("{} adet yeni ürün için stok oluşturuldu.", newStocks.size());

            return ResponseEntity.ok(newStocks.size() + " ürün için 100'er adet stok başarıyla eşitlendi! ✅");

        } catch (Exception e) {
            log.error("Stok senkronizasyon hatası", e);
            return ResponseEntity.internalServerError().body("Hata: " + e.getMessage());
        }
    }
}