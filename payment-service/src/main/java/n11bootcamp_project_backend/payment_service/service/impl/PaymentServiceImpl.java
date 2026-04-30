package n11bootcamp_project_backend.payment_service.service.impl;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.payment_service.entity.Payment;
import n11bootcamp_project_backend.payment_service.dto.request.PaymentRequest;
import n11bootcamp_project_backend.payment_service.dto.response.PaymentResponse;
import n11bootcamp_project_backend.payment_service.enums.PaymentStatus;
import n11bootcamp_project_backend.payment_service.repository.PaymentRepository;
import n11bootcamp_project_backend.payment_service.service.PaymentService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        //Ödeme yapıldı mı, yapıldıysa başarılı mı kontrol.
        if (paymentRepository.findByOrderIdAndStatus(
                request.orderId(), PaymentStatus.SUCCESS).isPresent()) {
            throw new RuntimeException("Payment already completed for this order");
        }

        // Iyzico Options
        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);

        // Ödeme kartı
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(request.cardHolderName());
        paymentCard.setCardNumber(request.cardNumber());
        paymentCard.setExpireMonth(request.expireMonth());
        paymentCard.setExpireYear(request.expireYear());
        paymentCard.setCvc(request.cvc());
        paymentCard.setRegisterCard(0);

        // Alıcı bilgileri
        Buyer buyer = new Buyer();
        buyer.setId(request.userId().toString());
        buyer.setName("Test");
        buyer.setSurname("User");
        buyer.setEmail("test@test.com");
        buyer.setIdentityNumber("74300864791");
        buyer.setRegistrationAddress("Test Address");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setIp("85.34.78.112");

        // Fatura adresi
        Address billingAddress = new Address();
        billingAddress.setContactName("Test User");
        billingAddress.setCity("Istanbul");
        billingAddress.setCountry("Turkey");
        billingAddress.setAddress("Test Address");

        // Teslimat adresi
        Address shippingAddress = new Address();
        shippingAddress.setContactName("Test User");
        shippingAddress.setCity("Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress("Test Address");

        // Sepet ürünü
        BasketItem basketItem = new BasketItem();
        basketItem.setId(request.orderId().toString());
        basketItem.setName("Order Payment");
        basketItem.setCategory1("General");
        basketItem.setItemType(BasketItemType.PHYSICAL.name());
        basketItem.setPrice(request.amount());

        // Ödeme isteği
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setLocale(Locale.TR.getValue());
        paymentRequest.setConversationId(request.orderId().toString());
        paymentRequest.setPrice(request.amount());
        paymentRequest.setPaidPrice(request.amount());
        paymentRequest.setCurrency(Currency.TRY.name());
        paymentRequest.setInstallment(1);
        paymentRequest.setBasketId(request.orderId().toString());
        paymentRequest.setPaymentChannel(PaymentChannel.WEB.name());
        paymentRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());
        paymentRequest.setPaymentCard(paymentCard);
        paymentRequest.setBuyer(buyer);
        paymentRequest.setShippingAddress(shippingAddress);
        paymentRequest.setBillingAddress(billingAddress);
        paymentRequest.setBasketItems(List.of(basketItem));

        // Iyzico'ya gönder
        com.iyzipay.model.Payment payment =
                com.iyzipay.model.Payment.create(paymentRequest, options);

        System.out.println("Iyzico Status: " + payment.getStatus());
        System.out.println("Iyzico Error: " + payment.getErrorMessage());
        System.out.println("Iyzico Error Code: " + payment.getErrorCode());

        // Sonucu değerlendir
        PaymentStatus status = "success".equals(payment.getStatus())
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;

        // DB'ye kaydet
        Payment savedPayment = paymentRepository.save(Payment.builder()
                .orderId(request.orderId())
                .userId(request.userId())
                .amount(request.amount())
                .status(status)
                .build());

        // RabbitMQ'ya event yay
        if (status == PaymentStatus.SUCCESS) {
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "payment.success",
                    request.orderId().toString()
            );
        } else {
            rabbitTemplate.convertAndSend(
                    "saga.exchange",
                    "payment.failed",
                    request.orderId().toString()
            );
        }

        return toResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return toResponse(payment);
    }

    // Entity → Response dönüşümü
    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}