package com.khy.demosaga.service;

import com.khy.demosaga.config.SagaConstants;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.producer.SagaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OtherService {
    private final SagaProducer sagaProducer;
    public void getAction(Saga saga, String consumeTopicName) {
        boolean isOk = true;
        setAction(saga, isOk, consumeTopicName);
    }
    public void setAction(Saga saga, Boolean isOk, String consumeTopicName) {
        log.info(">>> other service :: get current state =  {}", saga.getCurrentState().toString());
        String currentState = String.valueOf(saga.getCurrentState());
        String nextState = currentState;

        if (isOk) {
            nextState = nextState + "_OK";
        } else {
            nextState = nextState + "_FAIL";
        }

        Saga nextSaga = Saga.builder()
                .customerId(saga.getCustomerId())
                .orderId(saga.getOrderId())
                .productId(saga.getProductId())
                .amount(saga.getAmount())
                .shippingAddress(saga.getShippingAddress())
                .currentState(nextState)
                .eventAt(LocalDateTime.now())
                .build();

        log.info(">>> {} request ::::: {}", consumeTopicName, saga.toString());
        String produceTopicName = getProduceTopicName(consumeTopicName);

        log.info(">>> {} response ::::: {}", produceTopicName, nextSaga.toString());
        sagaProducer.send(produceTopicName, nextSaga.getCustomerId(), nextSaga);
    }

    private String getProduceTopicName(String consumeTopicName) throws IllegalStateException {
        switch(consumeTopicName) {
            case SagaConstants.DISCOUNT_REQUEST_TOPIC -> {
                return SagaConstants.DISCOUNT_RESPONSE_TOPIC;
            }
            case SagaConstants.PAYMENT_REQUEST_TOPIC -> {
                return SagaConstants.PAYMENT_RESPONSE_TOPIC;
            }
            default -> throw new IllegalStateException(">>> Unexpected value: " + consumeTopicName);
        }
    }
}
