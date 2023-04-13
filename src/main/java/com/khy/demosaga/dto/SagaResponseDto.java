package com.khy.demosaga.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SagaResponseDto(
        LocalDateTime eventTime,
        Long customerId,
        Long orderId,
        String currentState,
        String value
){}