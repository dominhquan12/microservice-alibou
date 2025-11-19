package com.develop.ecommerce.order;

import com.develop.ecommerce.customer.CustomerResponse;
import com.develop.ecommerce.kafka.OrderConfirmation;
import com.develop.ecommerce.customer.CustomerClient;
import com.develop.ecommerce.exception.BusinessException;
import com.develop.ecommerce.kafka.OrderProducer;
import com.develop.ecommerce.listener.OrderEventEnvelope;
import com.develop.ecommerce.listener.OrderStatus;
import com.develop.ecommerce.listener.PaymentNotificationRequest;
import com.develop.ecommerce.orderline.OrderLineRequest;
import com.develop.ecommerce.orderline.OrderLineService;
import com.develop.ecommerce.outbox.OrderOutbox;
import com.develop.ecommerce.outbox.OrderOutboxRepository;
import com.develop.ecommerce.outbox.OutboxStatus;
import com.develop.ecommerce.payment.PaymentClient;
import com.develop.ecommerce.payment.PaymentRequest;
import com.develop.ecommerce.product.ProductClient;
import com.develop.ecommerce.product.PurchaseRequest;
import com.develop.ecommerce.product.PurchaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper;
    private final OrderOutboxRepository orderOutboxRepository;
    @SneakyThrows
    @Transactional
    public Integer createOrder(OrderRequest request) {
        CustomerResponse customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        List<PurchaseResponse> purchasedProducts = productClient.purchaseProducts(request.products());

        Order order = this.repository.save(mapper.toOrder(request));

        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        order.setStatus(OrderStatus.CREATED);
        this.repository.save(order);

        BigDecimal totalAmount = purchasedProducts.stream()
                .map(p -> p.price().multiply(BigDecimal.valueOf(p.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentRequest paymentRequest = new PaymentRequest(
                totalAmount,
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );

        OrderOutbox outbox = new OrderOutbox();
        outbox.setEventType("PAYMENT_REQUESTED");
        OrderEventEnvelope envelope = new OrderEventEnvelope(
                "PAYMENT_REQUESTED",
                objectMapper.writeValueAsString(paymentRequest)
        );
        outbox.setPayload(objectMapper.writeValueAsString(envelope));
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setCreatedAt(Instant.now());
        orderOutboxRepository.save(outbox);

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }

    @SneakyThrows
    @Transactional
    public void approve(PaymentNotificationRequest paymentNotificationRequest) {
        String orderId = paymentNotificationRequest.orderId().toString();
        var order = repository.findById(Integer.parseInt(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        // Giả sử Order entity có field status
        order.setStatus(OrderStatus.APPROVED);
        repository.save(order);

        OrderOutbox outbox = new OrderOutbox();
        outbox.setEventType("NOTIFICATION_REQUESTED");
        OrderEventEnvelope envelope = new OrderEventEnvelope(
                "NOTIFICATION_REQUESTED",
                objectMapper.writeValueAsString(paymentNotificationRequest)
        );
        outbox.setPayload(objectMapper.writeValueAsString(envelope));
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setCreatedAt(Instant.now());
        orderOutboxRepository.save(outbox);
    }

    @Transactional
    public void cancel(PaymentNotificationRequest paymentNotificationRequest) {
        String orderId = paymentNotificationRequest.orderId().toString();
        var order = repository.findById(Integer.parseInt(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        order.setStatus(OrderStatus.CANCELLED);

        // Nếu muốn, trigger refund, release product, etc.
        repository.save(order);
    }

}
