package com.khy.demosaga.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Saga(LocalDateTime localDateTime, Long consumerId, Long orderId, String status, String value) {
}
