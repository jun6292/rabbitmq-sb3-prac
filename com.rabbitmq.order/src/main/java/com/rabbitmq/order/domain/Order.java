package com.rabbitmq.order.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Builder
@Data
@ToString
public class Order {
    private UUID orderId;
    private String userId;
    private String orderStatus; // Enum으로 변경 필요
    private String errorType;

    public void cancelOrder(String receiveErrorType) {
        orderStatus = "CANCELLED";
        errorType = receiveErrorType;
    }
}

