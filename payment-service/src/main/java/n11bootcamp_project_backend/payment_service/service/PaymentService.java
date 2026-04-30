package n11bootcamp_project_backend.payment_service.service;

import n11bootcamp_project_backend.payment_service.dto.request.PaymentRequest;
import n11bootcamp_project_backend.payment_service.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    // Ödeme başlat
    PaymentResponse processPayment(PaymentRequest request);

    // Ödeme bilgisi getir
    PaymentResponse getPaymentByOrderId(UUID orderId);
}