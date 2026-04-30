package n11bootcamp_project_backend.payment_service.repository;

import n11bootcamp_project_backend.payment_service.entity.Payment;
import n11bootcamp_project_backend.payment_service.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Sipariş ID'sine göre ödeme getir
    Optional<Payment> findByOrderId(UUID orderId);

    // Siparişi başarı durumuna göre ve idsine göre getir
    Optional<Payment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);

    //Orderın ödemesindeki en son durumu getir.
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(UUID orderId);
}