package n11bootcamp_project_backend.shopping_cart_service.service;

import n11bootcamp_project_backend.shopping_cart_service.dto.request.AddToCartRequest;
import n11bootcamp_project_backend.shopping_cart_service.dto.request.UpdateCartItemRequest;
import n11bootcamp_project_backend.shopping_cart_service.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    // Sepete ürün ekle
    CartResponse addToCart(UUID userId, AddToCartRequest request);

    // Sepeti getir
    CartResponse getCart(UUID userId);

    // Sepetteki ürün miktarını güncelle
    CartResponse updateCartItem(UUID userId, UUID productId, UpdateCartItemRequest request);

    // Sepetten ürün çıkar
    CartResponse removeFromCart(UUID userId, UUID productId);

    // Sepeti tamamen temizle (checkout sonrası)
    void clearCart(UUID userId);

    // Guest sepetini kullanıcı sepetine merge et
    CartResponse mergeCart(UUID guestId, UUID userId);
}