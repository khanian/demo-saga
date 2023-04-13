package com.khy.demosaga.dto;

import lombok.Builder;

@Builder
public record SagaRequestDto (
        Long customerId,
        Long orderId,
        String currentState,
        String value
){}