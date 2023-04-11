package com.khy.demosaga.service;

import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.producer.SagaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SagaService {

    private static final String SAGA_TOPIC = "saga-topic";
    private static final String DISCOUNT_TOPIC = "discount-topic";
    private static final String PAYMENT_TOPIC = "pay.request";
    private static final String PAYMENT_CANCEL_TOPIC = "saga.pay.pay.cancel";
    private static final String ORDER_TOPIC = "order-topic";
    private static final String ORDER_CANCEL_TOPIC = "saga.order.order.cancel";

    private final SagaProducer sagaProducer;
    private static SagaEvents sagaEvents;

//    private final StateMachineFactory<SagaStates, SagaEvents> factory;
//    private final StateMachine <SagaStates, SagaEvents> stateMachine;

    public void sendMessage(String topic, Saga saga) {
        sagaProducer.async(topic, saga);
    }

    public List<Saga> getNext(Saga saga) {
        List<Saga> nextStep = new ArrayList<>();
        List<Object> nextList = getNextStepList(SagaStates.valueOf(saga.currentState()));
        nextList.forEach(event -> {
            System.out.println(event);

            Saga nextSaga = Saga.builder()
                    .customerId(saga.customerId())
                    .orderId(saga.orderId())
                    .eventTime(LocalDateTime.now())
                    .currentState(String.valueOf(event))
                    .value("data|aaa|bbb")
                    .build();

            log.info("saga ::::: [{}]", saga.toString());
            log.info("next saga ::::: [{}]", nextSaga.toString());
            log.info("nextStep [0] = {}, [1] = {}", saga.currentState(), nextSaga.currentState());

            sagaProducer.asyncTest(event.toString()+"_TOPIC", saga);
            nextStep.add(saga);
        });
        return nextStep;
    }

    private static List<Object> getNextStepList (SagaStates state) {
        //Map<String, Object> nextEventMap = new HashMap<>();
        List<Object> listNext = new ArrayList<>();
        switch(state) {
            case ORDER_REQUEST -> {
                //nextEventMap.put("event", sagaEvents.DISCOUNT_QUERY);
                listNext.add(sagaEvents.DISCOUNT_QUERY);
            }
            case DISCOUNT_CHECK_OK -> {
                listNext.add(sagaEvents.POINT_QUERY);
            }
            case POINT_CHECK_OK -> {
                listNext.add(sagaEvents.DISCOUNT_REQUEST);
            }
            case DISCOUNT_REQUEST_OK -> {
                listNext.add(sagaEvents.PAYMENT_REQUEST);
            }
            case PAYMENT_REQUEST_OK -> {
                listNext.add(sagaEvents.ORDER_COMPLETE);
            }
            case PAYMENT_CANCEL_REQUEST, ORDER_CANCEL_REQUEST -> {
                listNext.add(sagaEvents.PAYMENT_CANCEL);
                listNext.add(sagaEvents.DISCOUNT_CANCEL);
                listNext.add(sagaEvents.ORDER_CANCEL);
            }
            case PAYMENT_REQUEST_FAIL, DISCOUNT_CANCEL_REQUEST -> {
                listNext.add(sagaEvents.DISCOUNT_CANCEL);
                listNext.add(sagaEvents.ORDER_CANCEL);
            }
            case DISCOUNT_CHECK_FAIL, POINT_CHECK_FAIL, DISCOUNT_REQUEST_FAIL -> {
                listNext.add(sagaEvents.ORDER_CANCEL);
            }
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
        return listNext;
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
