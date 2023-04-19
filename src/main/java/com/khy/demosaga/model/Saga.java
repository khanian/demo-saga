package com.khy.demosaga.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Saga {
    private Long orderId;
    private Long customerId;
    private Long productId;
    private Long amount;
    private String shippingAddress;
    private String currentState;
    private LocalDateTime eventAt;
}
