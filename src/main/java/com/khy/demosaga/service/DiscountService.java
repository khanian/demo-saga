package com.khy.demosaga.service;

import com.khy.demosaga.config.SagaConstants;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.producer.SagaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class DiscountService {

    private final SagaProducer sagaProducer;

    public void getAction(Saga saga) {
        boolean isOk = true;

        setAction(saga, isOk);
    }

    public void setAction(Saga saga, Boolean isOk) {
        log.info("get current state = :::: {}", saga.currentState().toString());
        String currentState = String.valueOf(saga.currentState());
        String nextState = currentState;
        if (isOk) {
            nextState = nextState+"_OK";
        } else {
            nextState = nextState+"_FAIL";
        }
        Saga nextSaga = Saga.builder()
                .customerId(saga.customerId())
                .orderId(saga.orderId())
                .productId(saga.productId())
                .amount(saga.amount())
                .eventAt(LocalDateTime.now())
                .currentState(SagaStates.valueOf(nextState))
                .build();

        log.info("Discount request ::::: [{}]", saga.toString());
        log.info("Discount response ::::: [{}]", nextSaga.toString());

        sagaProducer.send(SagaConstants.DISCOUNT_RESPONSE_TOPIC, nextSaga.customerId(), nextSaga);

    }
}
