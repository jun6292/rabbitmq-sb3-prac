package com.rabbitmq.product.controller;

import com.rabbitmq.product.domain.DeliveryMessage;
import com.rabbitmq.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEndpoint {
    private final ProductService productService;

    @RabbitListener(queues = "${message.queue.product}")
    public void receiveMessage(DeliveryMessage message) {
        log.info("Product RECEIVE: {}", message.toString());
        productService.reduceProductAmount(message);
    }

    @RabbitListener(queues = "${message.queue.err.product}")
    public void receiveErrMessage(DeliveryMessage message) {
        log.info("Product ERROR RECEIVE!!!");
        productService.rollbackProduct(message);
    }

}
