package com.khy.demosaga.service;

import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.config.StateMachineFactory;

@Slf4j
@Service
@AllArgsConstructor
public class sagaService {

    private final StateMachineFactory<SagaStates, SagaEvents> factory;
    private final StateMachine <SagaStates, SagaEvents> stateMachine;

    private static String getNextStepString (String state, SagaStates sagaStates, SagaEvents sagaEvents) {
        String nextEvent;
        switch(state) {
            case "ORDER_REQUEST" -> nextEvent = String.valueOf(sagaEvents.DISCOUNT_QUERY);
            case "DISCOUNT_CHECK_OK" -> nextEvent = String.valueOf(sagaEvents.POINT_QUERY);
            case "POINT_CHECK_OK" -> nextEvent = String.valueOf(sagaEvents.DISCOUNT_REQUEST);
            case "DISCOUNT_REQUEST_OK" -> nextEvent = String.valueOf(sagaEvents.PAYMENT_REQUEST);
            case "PAYMENT_REQUEST_OK" -> nextEvent = String.valueOf(sagaEvents.ORDER_COMPLETE);

            case "PAYMENT_CANCEL_REQUEST" -> nextEvent = String.valueOf(SagaEvents.PAYMENT_CANCEL);
            case "PAYMENT_REQUEST_FAIL", "DISCOUNT_CANCEL_REQUEST"
                    -> nextEvent = String.valueOf(SagaEvents.DISCOUNT_CANCEL);
            case "DISCOUNT_CHECK_FAIL", "POINT_CHECK_FAIL", "DISCOUNT_REQUEST_FAIL", "ORDER_CANCEL_REQUEST"
                    -> nextEvent = String.valueOf(SagaEvents.ORDER_CANCEL);
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
        return nextEvent;
    }

    private static String handleEvent(String currentState, StateMachine<SagaStates, SagaEvents> handleStateMachine) {
        log.info ("case sagaState ::: {}", currentState);
        String nextState = null;

        switch (currentState) {
            case "ORDER_REQUEST" -> {
                nextState = SagaStates.DISCOUNT_CHECKED.name();
                handleStateMachine.sendEvent(SagaEvents.DISCOUNT_QUERY);
                log.info("next state :: {}", nextState);
            }
            case "DISCOUNT_CHECK_OK" -> handleStateMachine.sendEvent(SagaEvents.POINT_QUERY);
            case "POINT_CHECK_OK" -> handleStateMachine.sendEvent(SagaEvents.DISCOUNT_REQUEST);
            case "DISCOUNT_REQUEST_OK" -> {
                System.out.println("여기 들어왔어......!!!");
                handleStateMachine.sendEvent(SagaEvents.PAYMENT_REQUEST);
            }
            case "PAYMENT_REQUEST_OK" -> handleStateMachine.sendEvent(SagaEvents.ORDER_COMPLETE);
            // cancel event
            case "PAYMENT_CANCEL_REQUEST" -> handleStateMachine.sendEvent(SagaEvents.PAYMENT_CANCEL);
            case "PAYMENT_REQUEST_FAIL", "DISCOUNT_CANCEL_REQUEST"
                    -> handleStateMachine.sendEvent(SagaEvents.DISCOUNT_CANCEL);
            case "DISCOUNT_CHECK_FAIL", "POINT_CHECK_FAIL", "DISCOUNT_REQUEST_FAIL", "ORDER_CANCEL_REQUEST"
                    -> handleStateMachine.sendEvent(SagaEvents.ORDER_CANCEL);
            default -> throw new IllegalStateException("Unexpected value: " + currentState);
        }
        
        return nextState;
    }

}
