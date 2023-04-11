package com.khy.demosaga.cotroller;

import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.service.SagaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {

    private final SagaService sagaService;

    @GetMapping("hello")
    public String getHello() {
        return "Hello, World";
    }

    @GetMapping("v1/next")
    public Saga getNext() {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(2L)
                .orderId(2L)
                .currentState(String.valueOf(SagaStates.DISCOUNT_REQUEST_OK))
                .value("")
                .build();
        return sagaService.getNext(saga);
    }

    @GetMapping("v1/test")
    public List<Saga> testEvent() {
        List<Saga> sagaList = new ArrayList<>();
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(2L)
                .orderId(2L)
                .currentState(String.valueOf(SagaStates.DISCOUNT_REQUEST_OK))
                .value("")
                .build();

        StateMachine<SagaStates, SagaEvents> sagaStateMachine = sagaService.getSagaStateMachine(saga);

        log.info("after calling sagaStateMachine() : {}", sagaStateMachine.getState().getId().name());

        sagaList.add(saga);
        Saga nextSaga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(2L)
                .orderId(2L)
                .currentState(sagaStateMachine.getState().getId().name())
                .value("")
                .build();
        sagaList.add(nextSaga);

        return sagaList;
    }
}
