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
    private LocalDateTime eventTime;
    private Long customerId;
    private Long orderId;
    private SagaStates currentState;
    private String value;
}
