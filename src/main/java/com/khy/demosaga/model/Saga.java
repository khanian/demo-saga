package com.khy.demosaga.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Saga(LocalDateTime eventTime, Long customerId, Long orderId, String currentState, String value) {
}
