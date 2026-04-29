package n11bootcamp_project_backend.shopping_cart_service.service.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.shopping_cart_service.dto.request.AddToCartRequest;
import n11bootcamp_project_backend.shopping_cart_service.dto.request.UpdateCartItemRequest;
import n11bootcamp_project_backend.shopping_cart_service.dto.response.CartItemResponse;
import n11bootcamp_project_backend.shopping_cart_service.dto.response.CartResponse;
import n11bootcamp_project_backend.shopping_cart_service.model.Cart;
import n11bootcamp_project_backend.shopping_cart_service.model.CartItem;
import n11bootcamp_project_backend.shopping_cart_service.service.CartService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis key prefix'leri
    private static final String USER_CART_PREFIX = "cart:user:";
    private static final String GUEST_CART_PREFIX = "cart:guest:";

    // TTL — kullanıcı sepeti 7 gün, guest sepeti 1 gün
    private static final long USER_CART_TTL = 7 * 24 * 60 * 60;
    private static final long GUEST_CART_TTL = 24 * 60 * 60;

    @Override
    public CartResponse addToCart(UUID userId, AddToCartRequest request) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);

        // Ürün zaten sepette var mı?
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Varsa miktarı artır
            existingItem.get().setQuantity(
                    existingItem.get().getQuantity() + request.quantity()
            );
        } else {
            // Yoksa yeni ekle
            CartItem newItem = CartItem.builder()
                    .productId(request.productId())
                    .productName(request.productName())
                    .price(request.price())
                    .quantity(request.quantity())
                    .build();
            cart.getItems().add(newItem);
        }

        saveCart(key, cart, USER_CART_TTL);
        return toResponse(cart);
    }

    @Override
    public CartResponse getCart(UUID userId) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);
        return toResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(UUID userId, UUID productId,
                                       UpdateCartItemRequest request) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);

        // Ürünü bul ve miktarı güncelle
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(request.quantity()));

        saveCart(key, cart, USER_CART_TTL);
        return toResponse(cart);
    }

    @Override
    public CartResponse removeFromCart(UUID userId, UUID productId) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);

        // Ürünü listeden çıkar
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        saveCart(key, cart, USER_CART_TTL);
        return toResponse(cart);
    }

    @Override
    public void clearCart(UUID userId) {
        redisTemplate.delete(USER_CART_PREFIX + userId);
    }

    @Override
    public CartResponse mergeCart(UUID guestId, UUID userId) {
        String guestKey = GUEST_CART_PREFIX + guestId;
        String userKey = USER_CART_PREFIX + userId;

        Cart guestCart = (Cart) redisTemplate.opsForValue().get(guestKey);
        Cart userCart = getOrCreateCart(userKey, userId);

        // Guest sepeti varsa merge et
        if (guestCart != null && !guestCart.getItems().isEmpty()) {
            for (CartItem guestItem : guestCart.getItems()) {
                Optional<CartItem> existingItem = userCart.getItems().stream()
                        .filter(item -> item.getProductId().equals(guestItem.getProductId()))
                        .findFirst();

                if (existingItem.isPresent()) {
                    // Aynı ürün varsa miktarları topla
                    existingItem.get().setQuantity(
                            existingItem.get().getQuantity() + guestItem.getQuantity()
                    );
                } else {
                    // Yoksa direkt ekle
                    userCart.getItems().add(guestItem);
                }
            }
            // Guest sepeti sil
            redisTemplate.delete(guestKey);
        }

        saveCart(userKey, userCart, USER_CART_TTL);
        return toResponse(userCart);
    }

    // Redis'ten sepeti getir, yoksa yeni oluştur
    private Cart getOrCreateCart(String key, UUID userId) {
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);
        if (cart == null) {
            cart = Cart.builder()
                    .userId(userId)
                    .items(new ArrayList<>())
                    .build();
        }
        return cart;
    }

    // Sepeti Redis'e kaydet
    private void saveCart(String key, Cart cart, long ttl) {
        redisTemplate.opsForValue().set(key, cart, ttl, TimeUnit.SECONDS);
    }

    // Cart → CartResponse dönüşümü
    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubTotal()
                ))
                .toList();

        return new CartResponse(
                cart.getUserId(),
                itemResponses,
                cart.getTotalPrice()
        );
    }
}