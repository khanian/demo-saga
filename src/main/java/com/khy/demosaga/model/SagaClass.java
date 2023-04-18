package com.khy.demosaga.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SagaClass {
    private Long orderId;
    private Long customerId;
    private Long productId;
    private Long amount;
    private SagaStates currentState;
    private LocalDateTime eventAt;
}
