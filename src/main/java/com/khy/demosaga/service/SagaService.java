package com.khy.demosaga.service;

import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.producer.SagaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class SagaService {

    private static final String SAGA_TOPIC = "saga-topic";
    private static final String DISCOUNT_TOPIC = "discount-topic";
    private static final String PAYMENT_TOPIC = "payment-topic";
    private static final String ORDER_TOPIC = "order-topic";

    private final SagaProducer sagaProducer;
    private static SagaEvents sagaEvents;

    //    private final StateMachineFactory<SagaStates, SagaEvents> factory;
//    private final StateMachine <SagaStates, SagaEvents> stateMachine;

    public void sendMessage(String topic, Saga saga) {
        sagaProducer.async(topic, saga);
    }

    public Saga getNext(Saga saga) {
        String[] nextStep = getNextStepString(saga.currentState());
        Saga nextSaga = Saga.builder()
                .customerId(saga.customerId())
                .orderId(saga.orderId())
                .eventTime(LocalDateTime.now())
                .currentState(nextStep[0])
                .value("data|aaa|bbb")
                .build();

        log.info("saga ::::: [{}]", saga.toString());
        log.info("next saga ::::: [{}]", nextSaga.toString());
        log.info("nextStep [0] = {}, [1] = {}", nextStep[0], nextStep[1]);

        sagaProducer.asyncTest(nextStep[1], saga);
        log.info("done!!!!");
        return nextSaga;
    }


    private static String[] getNextStepString (String state) {
        String[] nextEvent = new String[2];
        switch(state) {
            case "ORDER_REQUEST" -> {
                nextEvent[0] = String.valueOf(sagaEvents.DISCOUNT_QUERY);
                nextEvent[1] = DISCOUNT_TOPIC;
            }
            case "DISCOUNT_CHECK_OK" -> {
                nextEvent[0] = String.valueOf(sagaEvents.POINT_QUERY);
                nextEvent[1] = PAYMENT_TOPIC;
            }
            case "POINT_CHECK_OK" -> {
                nextEvent[0] = String.valueOf(sagaEvents.DISCOUNT_REQUEST);
                nextEvent[1] = DISCOUNT_TOPIC;
            }
            case "DISCOUNT_REQUEST_OK" -> {
                nextEvent[0] = String.valueOf(sagaEvents.PAYMENT_REQUEST);
                nextEvent[1] = PAYMENT_TOPIC;
            }
            case "PAYMENT_REQUEST_OK" -> {
                nextEvent[0] = String.valueOf(sagaEvents.ORDER_COMPLETE);
                nextEvent[1] = ORDER_TOPIC;
            }
            case "PAYMENT_CANCEL_REQUEST" -> {
                nextEvent[0] = String.valueOf(SagaEvents.PAYMENT_CANCEL);
                nextEvent[1] = PAYMENT_TOPIC;
            }
            case "PAYMENT_REQUEST_FAIL", "DISCOUNT_CANCEL_REQUEST" -> {
                nextEvent[0] = String.valueOf(SagaEvents.DISCOUNT_CANCEL);
                nextEvent[1] = DISCOUNT_TOPIC;
            }
            case "DISCOUNT_CHECK_FAIL", "POINT_CHECK_FAIL", "DISCOUNT_REQUEST_FAIL", "ORDER_CANCEL_REQUEST" -> {
                nextEvent[0] = String.valueOf(SagaEvents.ORDER_CANCEL);
                nextEvent[1] = ORDER_TOPIC;
            }
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
        return nextEvent;
    }

//    private static String handleEvent(String currentState, StateMachine<SagaStates, SagaEvents> handleStateMachine) {
//        log.info ("case sagaState ::: {}", currentState);
//        String nextState = null;
//
//        switch (currentState) {
//            case "ORDER_REQUEST" -> {
//                nextState = SagaStates.DISCOUNT_CHECKED.name();
//                handleStateMachine.sendEvent(SagaEvents.DISCOUNT_QUERY);
//                log.info("next state :: {}", nextState);
//            }
//            case "DISCOUNT_CHECK_OK" -> handleStateMachine.sendEvent(SagaEvents.POINT_QUERY);
//            case "POINT_CHECK_OK" -> handleStateMachine.sendEvent(SagaEvents.DISCOUNT_REQUEST);
//            case "DISCOUNT_REQUEST_OK" -> {
//                System.out.println("여기 들어왔어......!!!");
//                handleStateMachine.sendEvent(SagaEvents.PAYMENT_REQUEST);
//            }
//            case "PAYMENT_REQUEST_OK" -> handleStateMachine.sendEvent(SagaEvents.ORDER_COMPLETE);
//            // cancel event
//            case "PAYMENT_CANCEL_REQUEST" -> handleStateMachine.sendEvent(SagaEvents.PAYMENT_CANCEL);
//            case "PAYMENT_REQUEST_FAIL", "DISCOUNT_CANCEL_REQUEST"
//                    -> handleStateMachine.sendEvent(SagaEvents.DISCOUNT_CANCEL);
//            case "DISCOUNT_CHECK_FAIL", "POINT_CHECK_FAIL", "DISCOUNT_REQUEST_FAIL", "ORDER_CANCEL_REQUEST"
//                    -> handleStateMachine.sendEvent(SagaEvents.ORDER_CANCEL);
//            default -> throw new IllegalStateException("Unexpected value: " + currentState);
//        }
//
//        return nextState;
//    }

}
