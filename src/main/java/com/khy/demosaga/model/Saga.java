package com.khy.demosaga.model;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record Saga (
    Long orderId,
    Long customerId,
    Long productId,
    Long amount,
    SagaStates currentState,
    LocalDateTime eventAt
){}
