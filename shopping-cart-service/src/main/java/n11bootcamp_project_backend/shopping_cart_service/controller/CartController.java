package n11bootcamp_project_backend.shopping_cart_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.shopping_cart_service.dto.request.AddToCartRequest;
import n11bootcamp_project_backend.shopping_cart_service.dto.request.UpdateCartItemRequest;
import n11bootcamp_project_backend.shopping_cart_service.dto.response.CartResponse;
import n11bootcamp_project_backend.shopping_cart_service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Sepete ürün ekle
    @PostMapping("/{userId}")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable UUID userId,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCart(userId, request));
    }

    // Sepeti getir
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    // Sepetteki ürün miktarını güncelle
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable UUID userId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, request));
    }

    // Sepetten ürün çıkar
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    // Sepeti temizle
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // Guest sepetini kullanıcı sepetine merge et
    @PostMapping("/merge")
    public ResponseEntity<CartResponse> mergeCart(
            @RequestParam UUID guestId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(cartService.mergeCart(guestId, userId));
    }
}