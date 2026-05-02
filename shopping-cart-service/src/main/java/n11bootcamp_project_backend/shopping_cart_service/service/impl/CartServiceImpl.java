package n11bootcamp_project_backend.shopping_cart_service.service.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.producer.LogProducer;
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
    private final LogProducer logProducer;

    private static final String USER_CART_PREFIX = "cart:user:";
    private static final String GUEST_CART_PREFIX = "cart:guest:";
    private static final long USER_CART_TTL = 7 * 24 * 60 * 60;
    private static final long GUEST_CART_TTL = 24 * 60 * 60;

    @Override
    public CartResponse addToCart(UUID userId, AddToCartRequest request) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(
                    existingItem.get().getQuantity() + request.quantity()
            );
            logProducer.sendLog("shopping-cart-service", "INFO", "Product quantity increased in cart. User: " + userId + ", Product: " + request.productId());
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(request.productId())
                    .productName(request.productName())
                    .price(request.price())
                    .quantity(request.quantity())
                    .build();
            cart.getItems().add(newItem);
            logProducer.sendLog("shopping-cart-service", "INFO", "New product added to cart. User: " + userId + ", Product: " + request.productId());
        }

        saveCart(key, cart, USER_CART_TTL);
        return toResponse(cart);
    }

    @Override
    public CartResponse getCart(UUID userId) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);
        logProducer.sendLog("shopping-cart-service", "INFO", "Cart retrieved for user: " + userId);
        return toResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(UUID userId, UUID productId, UpdateCartItemRequest request) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);

        boolean found = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .map(item -> {
                    item.setQuantity(request.quantity());
                    return true;
                }).orElse(false);

        if (found) {
            saveCart(key, cart, USER_CART_TTL);
            logProducer.sendLog("shopping-cart-service", "INFO", "Cart item updated. User: " + userId + ", Product: " + productId + ", New Quantity: " + request.quantity());
        } else {
            logProducer.sendLog("shopping-cart-service", "WARN", "Update failed: Product not found in cart. User: " + userId + ", Product: " + productId);
        }

        return toResponse(cart);
    }

    @Override
    public CartResponse removeFromCart(UUID userId, UUID productId) {
        String key = USER_CART_PREFIX + userId;
        Cart cart = getOrCreateCart(key, userId);

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (removed) {
            saveCart(key, cart, USER_CART_TTL);
            logProducer.sendLog("shopping-cart-service", "INFO", "Product removed from cart. User: " + userId + ", Product: " + productId);
        } else {
            logProducer.sendLog("shopping-cart-service", "WARN", "Remove failed: Product not in cart. User: " + userId + ", Product: " + productId);
        }

        return toResponse(cart);
    }

    @Override
    public void clearCart(UUID userId) {
        redisTemplate.delete(USER_CART_PREFIX + userId);
        logProducer.sendLog("shopping-cart-service", "INFO", "Cart cleared for user: " + userId);
    }

    @Override
    public CartResponse mergeCart(UUID guestId, UUID userId) {
        String guestKey = GUEST_CART_PREFIX + guestId;
        String userKey = USER_CART_PREFIX + userId;

        Cart guestCart = (Cart) redisTemplate.opsForValue().get(guestKey);
        Cart userCart = getOrCreateCart(userKey, userId);

        if (guestCart != null && !guestCart.getItems().isEmpty()) {
            for (CartItem guestItem : guestCart.getItems()) {
                Optional<CartItem> existingItem = userCart.getItems().stream()
                        .filter(item -> item.getProductId().equals(guestItem.getProductId()))
                        .findFirst();

                if (existingItem.isPresent()) {
                    existingItem.get().setQuantity(
                            existingItem.get().getQuantity() + guestItem.getQuantity()
                    );
                } else {
                    userCart.getItems().add(guestItem);
                }
            }
            redisTemplate.delete(guestKey);
            logProducer.sendLog("shopping-cart-service", "INFO", "Guest cart merged into user cart. GuestID: " + guestId + ", UserID: " + userId);
        }

        saveCart(userKey, userCart, USER_CART_TTL);
        return toResponse(userCart);
    }

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

    private void saveCart(String key, Cart cart, long ttl) {
        try {
            redisTemplate.opsForValue().set(key, cart, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            logProducer.sendLog("shopping-cart-service", "ERROR", "Redis save failed for key: " + key + ". Error: " + e.getMessage());
            throw e;
        }
    }

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