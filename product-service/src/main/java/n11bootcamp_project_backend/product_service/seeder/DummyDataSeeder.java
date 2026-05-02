package n11bootcamp_project_backend.product_service.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.product_service.entity.Category;
import n11bootcamp_project_backend.product_service.entity.Product;
import n11bootcamp_project_backend.product_service.repository.CategoryRepository;
import n11bootcamp_project_backend.product_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DummyDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Veritabanı boş. 8 Kategori ve 80 Ürün yükleniyor...");
            seedData();
            log.info("✅ Tüm dummy veriler başarıyla yüklendi!");
        } else {
            log.info("Veritabanında halihazırda ürünler mevcut, seeder atlandı.");
        }
    }

    /**
     * Her ürün kendi görseline sahip.
     * Yapı: kategori adı -> List<String[2]> (productName, imageUrl)
     */
    private void seedData() {

        // LinkedHashMap sırayı korur
        Map<String, List<String[]>> data = new LinkedHashMap<>();

        // ── Elektronik ──────────────────────────────────────────────────────────
        data.put("Elektronik", List.of(
                new String[]{"Akıllı Telefon 128GB",            "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80"},
                new String[]{"15.6 inç Dizüstü Bilgisayar",    "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&q=80"},
                new String[]{"Gürültü Engelleyici Kulaklık",    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80"},
                new String[]{"Akıllı Saat",                     "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80"},
                new String[]{"10 inç Tablet",                   "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&q=80"},
                new String[]{"4K Ultra HD Televizyon",          "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=800&q=80"},
                new String[]{"Oyun Konsolu 1TB",                "https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=800&q=80"},
                new String[]{"Bluetooth Taşınabilir Hoparlör",  "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=800&q=80"},
                new String[]{"20000mAh Powerbank",              "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=800&q=80"},
                new String[]{"Mekanik Oyuncu Klavyesi",         "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=800&q=80"}
        ));

        // ── Giyim & Moda ─────────────────────────────────────────────────────────
        data.put("Giyim & Moda", List.of(
                new String[]{"Pamuklu Basic Tişört",            "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=800&q=80"},
                new String[]{"Slim Fit Kot Pantolon",           "https://images.unsplash.com/photo-1542272604-787c3835535d?w=800&q=80"},
                new String[]{"Beyaz Uzun Kollu Penye",            "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=800&q=80"},
                new String[]{"Su Geçirmez Kışlık Mont",         "https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=800&q=80"},
                new String[]{"Günlük Spor Ayakkabı",            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80"},
                new String[]{"Hakiki Deri Ceket",               "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&q=80"},
                new String[]{"Çiçek Desenli Yazlık Elbise",     "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&q=80"},
                new String[]{"Polarize Güneş Gözlüğü",         "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800&q=80"},
                new String[]{"Klasik Kesim Gömlek",             "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800&q=80"},
                new String[]{"Original Tshirt",                       "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=800&q=80"}
        ));

        // ── Ev & Yaşam ───────────────────────────────────────────────────────────
        data.put("Ev & Yaşam", List.of(
                new String[]{"Tam Ortopedik Yatak",             "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80"},
                new String[]{"Ahşap Çalışma Masası",            "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&q=80"},
                new String[]{"Modern Dekoratif Lamba",          "https://images.unsplash.com/photo-1543198126-a8ad8e47fb22?w=800&q=80"},
                new String[]{"Çift Kişilik Pamuklu Nevresim",   "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80"},
                new String[]{"Mini Fırın",           "https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=800&q=80"},
                new String[]{"Mini Beyaz Masa",           "https://images.unsplash.com/photo-1520970014086-2208d157c9e2?w=800&q=80"},
                new String[]{"Siyah Küçük Fide Kutusu",            "https://images.unsplash.com/photo-1589923188900-85dae523342b?w=800&q=80"},
                new String[]{"Ergonomik Ofis Koltuğu",          "https://images.unsplash.com/photo-1505843490538-5133c6c7d0e1?w=800&q=80"},
                new String[]{"Çok Raflı Kitaplık",                "https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&q=80"},
                new String[]{"Minimalist Duvar Saati",          "https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=800&q=80"}
        ));

        // ── Spor & Outdoor ───────────────────────────────────────────────────────
        data.put("Spor & Outdoor", List.of(
                new String[]{"4 Kişilik Kamp Çadırı",           "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=800&q=80"},
                new String[]{"Kaymaz Pilates Matı",             "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800&q=80"},
                new String[]{"Ayarlanabilir Dumbell Seti",      "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=800&q=80"},
                new String[]{"21 Vites Dağ Bisikleti",          "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=800&q=80"},
                new String[]{"Profesyonel Koşu Ayakkabısı",     "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80"},
                new String[]{"Paslanmaz Çelik Termos",          "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800&q=80"},
                new String[]{"Kamp Marshmelow Çubukları",          "https://images.unsplash.com/photo-1510672981848-a1c4f1cb5ccf?w=800&q=80"},
                new String[]{"Geniş Spor Çantası",              "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80"},
                new String[]{"Direnç İpi",               "https://images.unsplash.com/photo-1517344884509-a0c97ec11bcc?w=800&q=80"},
                new String[]{"Güneş Gözlüğü",    "https://images.unsplash.com/photo-1621184455862-c163dfb30e0f?w=800&q=80"}
        ));

        // ── Kozmetik & Bakım ─────────────────────────────────────────────────────
        data.put("Kozmetik & Bakım", List.of(
                new String[]{"Yoğun Nemlendirici Krem",         "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"},
                new String[]{"C Vitamini Yüz Serumu",           "https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=800&q=80"},
                new String[]{"Odunsu Notalı Erkek Parfümü",     "https://images.unsplash.com/photo-1587017539504-67cfbddac569?w=800&q=80"},
                new String[]{"50 SPF Güneş Kremi",              "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800&q=80"},
                new String[]{"Anti-Aging Göz Altı Kremi",       "https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=800&q=80"},
                new String[]{"Oje Çıkarıcı",                  "https://images.unsplash.com/photo-1599305090598-fe179d501227?w=800&q=80"},
                new String[]{"Argan Yağlı Saç Bakım Serumu",   "https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=800&q=80"},
                new String[]{"Şarjlı Tıraş Makinesi",          "https://images.unsplash.com/photo-1621607512022-6aecc4fed814?w=800&q=80"},
                new String[]{"Profesyonel Makyaj Fırçası Seti", "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=800&q=80"},
                new String[]{"Derinlemesine Yüz Temizleme Jeli","https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=800&q=80"}
        ));

        // ── Kitap & Kırtasiye ────────────────────────────────────────────────────
        data.put("Kitap & Kırtasiye", List.of(
                new String[]{"Dünya Klasikleri Seti (5 Kitap)", "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=800&q=80"},
                new String[]{"Ödüllü Bilim Kurgu Romanı",       "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=800&q=80"},
                new String[]{"Premium Tükenmez Kalem Seti",     "https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?w=800&q=80"},
                new String[]{"Deri Kapaklı Çizgisiz Defter",   "https://images.unsplash.com/photo-1531346878377-a5be20888e57?w=800&q=80"},
                new String[]{"2026 Masaüstü Ajanda",            "https://images.unsplash.com/photo-1506784365847-bbad939e9335?w=800&q=80"},
                new String[]{"Pastel Renkli Fosforlu Kalem Seti","https://images.unsplash.com/photo-1456735190827-d1262f71b8a3?w=800&q=80"},
                new String[]{"Grafik Çizim Tableti",            "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=800&q=80"},
                new String[]{"Kıskaçlı Okuma Lambası",          "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800&q=80"},
                new String[]{"Su Geçirmez Sırt Çantası",        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80"},
                new String[]{"24 Renk Sulu Boya Seti",          "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=800&q=80"}
        ));

        // ── Oyuncak & Hobi ───────────────────────────────────────────────────────
        data.put("Oyuncak & Hobi", List.of(
                new String[]{"1000 Parça Yapboz",               "https://images.unsplash.com/photo-1605106702734-205df224ecce?w=800&q=80"},
                new String[]{"Uzaktan Kumandalı Arazi Arabası", "https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?w=800&q=80"},
                new String[]{"Strateji Kutu Oyunu",             "https://images.unsplash.com/photo-1611996575749-79a3a250f948?w=800&q=80"},
                new String[]{"Sevimli Peluş Ayıcık",            "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=800&q=80"},
                new String[]{"Eğitici Ahşap Bloklar",           "https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=800&q=80"},
                new String[]{"Kameralı Mini Drone",             "https://images.unsplash.com/photo-1473968512647-3e447244af8f?w=800&q=80"},
                new String[]{"Koleksiyonluk Aksiyon Figürü",    "https://images.unsplash.com/photo-1608278047522-58806a6ac85b?w=800&q=80"},
                new String[]{"Zeka Geliştirici Akıl Oyunları",  "https://images.unsplash.com/photo-1606503153255-59d8b8b82176?w=800&q=80"},
                new String[]{"Yetişkinler İçin Boyama Kitabı",  "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=800&q=80"},
                new String[]{"Maket Uçak Yapım Seti",           "https://images.unsplash.com/photo-1474302770737-173ee21bab63?w=800&q=80"}
        ));

        // ── Süpermarket ──────────────────────────────────────────────────────────
        data.put("Süpermarket", List.of(
                new String[]{"Yöresel Filtre Kahve 250g",       "https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=800&q=80"},
                new String[]{"Soğuk Sıkım Zeytinyağı 1L",      "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=800&q=80"},
                new String[]{"Çikolata Paketi"          ,      "https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=800&q=80"},
                new String[]{"Organik Yulaf Ezmesi",            "https://images.unsplash.com/photo-1614961233913-a5113a4a34ed?w=800&q=80"},
                new String[]{"Kış Çayı Paketi",                 "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=800&q=80"},
                new String[]{"Kurabiye Paketi   ",             "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800&q=80"},
                new String[]{"Bitter Çikolata Seti",            "https://images.unsplash.com/photo-1481391319762-47dff72954d9?w=800&q=80"},
                new String[]{"Şekersiz Meyveli Granola",        "https://images.unsplash.com/photo-1542691457-cbe4df041eb2?w=800&q=80"},
                new String[]{"Tam Buğday Penne Makarna",        "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=800&q=80"},
                new String[]{"Gong Çikolatalı Dondurma"     ,   "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=800&q=80"}
        ));

        // ── Veritabanına kaydet ──────────────────────────────────────────────────
        for (Map.Entry<String, List<String[]>> entry : data.entrySet()) {
            String categoryName = entry.getKey();
            List<String[]> products = entry.getValue();

            Category category = Category.builder()
                    .name(categoryName)
                    .description(categoryName + " ürünleri")
                    .build();
            Category savedCategory = categoryRepository.save(category);

            List<Product> productsToSave = new ArrayList<>();

            for (String[] product : products) {
                String productName = product[0];
                String imageUrl    = product[1];

                double randomPrice = 50 + (4950 * random.nextDouble());
                BigDecimal price = BigDecimal.valueOf(randomPrice).setScale(2, RoundingMode.HALF_UP);

                productsToSave.add(Product.builder()
                        .name(productName)
                        .description(productName + " için detaylı açıklama.")
                        .price(price)
                        .imageUrl(imageUrl)
                        .category(savedCategory)
                        .build());
            }

            productRepository.saveAll(productsToSave);
        }
    }
}