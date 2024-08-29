package com.rabbitmq.order.service;

import com.rabbitmq.order.controller.OrderEndpoint;
import com.rabbitmq.order.domain.DeliveryMessage;
import com.rabbitmq.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${message.queue.product}")
    private String productQueue;

    private final RabbitTemplate rabbitTemplate;

    private Map<UUID, Order> orderStore = new HashMap<>();

    public Order createOrder(OrderEndpoint.OrderRequestDto orderRequestDto) {
        Order order = orderRequestDto.toOrder();
        DeliveryMessage deliveryMessage = orderRequestDto.toDeliveryMessage(order.getOrderId());

        orderStore.put(order.getOrderId(), order);

        log.info("send Message : {}", deliveryMessage.toString());

        // product queue에 message 적재
        rabbitTemplate.convertAndSend(productQueue, deliveryMessage);

        return order;

    }

    public void rollbackOrder(DeliveryMessage message) {
        Order order = orderStore.get(message.getOrderId());
        order.cancelOrder(message.getErrorType());
        log.info(order.toString());
    }

    public Order getOrder(UUID orderId) {
        return orderStore.get(orderId);
    }
}