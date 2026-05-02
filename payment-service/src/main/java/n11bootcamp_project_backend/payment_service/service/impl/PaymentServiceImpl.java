package n11bootcamp_project_backend.payment_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.payment_service.entity.Payment;
import n11bootcamp_project_backend.payment_service.dto.request.PaymentRequest;
import n11bootcamp_project_backend.payment_service.dto.response.PaymentResponse;
import n11bootcamp_project_backend.payment_service.enums.PaymentStatus;
import n11bootcamp_project_backend.payment_service.repository.PaymentRepository;
import n11bootcamp_project_backend.payment_service.service.PaymentService;
import n11bootcamp_project_backend.producer.LogProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j // System.out.println yerine loglama kullanmak için eklendi
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LogProducer logProducer;


    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    // Magic Strings
    private static final String SAGA_EXCHANGE = "saga.exchange";
    private static final String ROUTING_KEY_SUCCESS = "payment.success";
    private static final String ROUTING_KEY_FAILED = "payment.failed";
    private final ObjectMapper objectMapper;

    @Override
    @Transactional // İşlemlerin yarıda kalmasını önlemek için eklendi
    public PaymentResponse processPayment(UUID orderId) {

        String redisKey = "payment:" + orderId;

        // 1. Redis'ten saf JSON string'ini çektik
        String jsonValue = (String) redisTemplate.opsForValue().get(redisKey);

        if (jsonValue == null) {
            log.error("Payment details not found in Redis for orderId: {}", orderId);
            logProducer.sendLog("payment-service", "ERROR", "Payment details not found in Redis for orderId: " + orderId);
            publishSagaEvent(PaymentStatus.FAILED, orderId); // Saga'yı iptal et
            throw new RuntimeException("Payment timeout or details not found");
        }

        // 2. ObjectMapper ile JSON String'ini PaymentRequest nesnesine çeviriyoruz
        PaymentRequest request;
        try {
            request = objectMapper.readValue(jsonValue, PaymentRequest.class);
        } catch (Exception e) {
            log.error("JSON parsing error for orderId: {}", orderId, e);
            logProducer.sendLog("payment-service", "ERROR", "JSON parsing error for orderId: " + orderId);
            throw new RuntimeException("Data corruption in Redis");
        }

        // 3. Çift çekimi önlemek için DB kontrolü
        if (paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.SUCCESS).isPresent()) {
            logProducer.sendLog("payment-service", "ERROR", "Payment already completed for this order.");
            throw new RuntimeException("Payment already completed for this order");
        }

        log.info("Processing payment for orderId: {}, amount: {}", orderId, request.amount());

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
        com.iyzipay.model.Payment payment = com.iyzipay.model.Payment.create(paymentRequest, options);

        log.info("Iyzico Status: {}", payment.getStatus());
        if (!"success".equals(payment.getStatus())) {
            log.error("Iyzico Error: {} - Error Code: {}", payment.getErrorMessage(), payment.getErrorCode());
            logProducer.sendLog("payment-service", "ERROR", "Iyzico Error: " + payment.getErrorMessage() + "- Error Code: " + payment.getErrorCode());
        }

        // Sonucu değerlendir
        PaymentStatus status = "success".equals(payment.getStatus())
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;

        if (status == PaymentStatus.SUCCESS) {
            logProducer.sendLog("payment-service", "INFO", "Payment completed successfully for order id: " + orderId);
        }else{
            logProducer.sendLog("payment-service", "ERROR", "Payment failed for order id: " + orderId);
        }

        // DB'ye kaydet
        Payment savedPayment = paymentRepository.save(Payment.builder()
                .orderId(request.orderId())
                .userId(request.userId())
                .amount(request.amount())
                .status(status)
                .build());

        // RabbitMQ'ya event yay
        publishSagaEvent(status, request.orderId());

        redisTemplate.delete(redisKey);
        log.info("Payment details removed from Redis for orderId: {}", orderId);
        logProducer.sendLog("payment-service", "INFO", "Payment details removed from Redis for orderId: " + orderId);

        return toResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)

                .orElseThrow(() -> {
                    logProducer.sendLog("payment-service", "ERROR", "Payment not found could not found for orderId: " + orderId);
                    return new RuntimeException("Payment could not found");
                });
        return toResponse(payment);
    }

    // RabbitMQ Mesaj Gönderim Metodu
    private void publishSagaEvent(PaymentStatus status, UUID orderId) {
        String routingKey = (status == PaymentStatus.SUCCESS) ? ROUTING_KEY_SUCCESS : ROUTING_KEY_FAILED;

        log.info("Publishing saga event to {} with routing key {} for orderId {}", SAGA_EXCHANGE, routingKey, orderId);
        logProducer.sendLog("payment-service", "INFO", "Publishing saga event to " + SAGA_EXCHANGE + " with routing key " + routingKey + " for orderId: " + orderId);

        rabbitTemplate.convertAndSend(
                SAGA_EXCHANGE,
                routingKey,
                orderId.toString()
        );
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