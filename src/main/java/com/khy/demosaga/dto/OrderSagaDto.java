package com.khy.demosaga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderSagaDto {
    private Long orderId;
    private Long customerId;
    private Long productId;
    private Long amount;
    private String shippingAddress;
    private String currentState;
    private LocalDateTime eventAt;
}