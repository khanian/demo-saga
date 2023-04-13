package com.khy.demosaga.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Saga (
        Long orderId,
        Long customerId,
        LocalDateTime eventTime,
        SagaStates currentState,
        String value
){}
