package com.rabbitmq.product.service;

import com.rabbitmq.product.domain.DeliveryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    @Value("${message.queue.payment}")
    private String paymentQueue;

    @Value("${message.queue.err.order}")
    private String orderErrQueue;

    private final RabbitTemplate rabbitTemplate;

    public void reduceProductAmount(DeliveryMessage message) {
        Integer productId = message.getProductId();
        Integer productQuantity = message.getProductQuantity();

        if (productId != 1 || productQuantity > 1) {
            this.rollbackProduct(message);
            return;
        }

        // payment queue에 message 적재
        rabbitTemplate.convertAndSend(paymentQueue, message);
    }


    public void rollbackProduct(DeliveryMessage message) {
        log.info("Product ROLLBACK!!!");
        if (!StringUtils.hasText(message.getErrorType())) {
            message.setErrorType("PRODUCT ERROR");
        }
        rabbitTemplate.convertAndSend(orderErrQueue, message);
    }
}
