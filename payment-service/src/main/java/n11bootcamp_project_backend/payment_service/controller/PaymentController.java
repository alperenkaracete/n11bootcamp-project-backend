package n11bootcamp_project_backend.payment_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.payment_service.dto.request.PaymentRequest;
import n11bootcamp_project_backend.payment_service.dto.response.PaymentResponse;
import n11bootcamp_project_backend.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Ödeme başlat
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(request));
    }

    // Sipariş ID'sine göre ödeme getir
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
