package com.rabbitmq.payment.service;

import com.rabbitmq.payment.domain.DeliveryMessage;
import com.rabbitmq.payment.domain.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${message.queue.err.product}")
    private String productErrQueue;

    private final RabbitTemplate rabbitTemplate;

    public void createPayment(DeliveryMessage message) {
        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .userId(message.getUserId())
                .payAmount(message.getPayAmount())
                .payStatus("SUCCESS")
                .build();

        Integer payAmount = message.getPayAmount();
        if (payAmount >= 10000) {
            message.setErrorType("PAYMENT_LIMIT_EXCEEDED");
            log.error("Payment amount exceeds the limit: {}", payAmount);
            this.rollbackPayment(message);
        }
    }

    private void rollbackPayment(DeliveryMessage message) {
        log.info("Rollback payment");
        rabbitTemplate.convertAndSend(productErrQueue, message);
    }
}
