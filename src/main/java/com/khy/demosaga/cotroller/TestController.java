package com.khy.demosaga.cotroller;

import com.khy.demosaga.dto.SagaRequestDto;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.service.SagaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("v1/discount/check/ok")
    public Saga getDiscountCheckOK() {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(1L)
                .orderId(1L)
                .currentState(SagaStates.DISCOUNT_CHECK_OK)
                .value("")
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }
    @GetMapping("v1/discount/check/fail")
    public Saga getDiscountCheckFail() {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(1L)
                .orderId(1L)
                .currentState(SagaStates.DISCOUNT_CHECK_FAIL)
                .value("")
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }

    @GetMapping("v1/point/check/ok")
    public Saga getPointCheckOK() {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(1L)
                .orderId(1L)
                .currentState(SagaStates.POINT_CHECK_OK)
                .value("")
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }
    @GetMapping("v1/point/check/fail")
    public Saga getPointCheckFail() {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(1L)
                .orderId(1L)
                .currentState(SagaStates.POINT_CHECK_FAIL)
                .value("")
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }

    @GetMapping("v1/next")
    public Saga getNext() {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(1L)
                .orderId(1L)
                .currentState(SagaStates.DISCOUNT_CHECK_OK)
                .value("")
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }

    @PostMapping("v1/nextPost")
    public Saga sendNext(@RequestBody SagaRequestDto sagaDto) {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(sagaDto.customerId())
                .orderId(sagaDto.customerId())
                .currentState(SagaStates.valueOf(sagaDto.currentState()))
                .value("")
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }

    @GetMapping("v1/test")
    public List<Saga> testEvent() {
        List<Saga> sagaList = new ArrayList<>();
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(2L)
                .orderId(2L)
                .currentState(SagaStates.DISCOUNT_REQUEST_OK)
                .value("")
                .build();

        StateMachine<SagaStates, SagaEvents> sagaStateMachine = sagaService.getStateMachine(saga);
        log.info("after calling sagaStateMachine() : {}", sagaStateMachine.getState().getId().name());

        sagaList.add(saga);
        Saga nextSaga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(saga.customerId())
                .orderId(saga.orderId())
                .currentState(sagaStateMachine.getState().getId())
                .value("")
                .build();
        sagaList.add(nextSaga);

        return sagaList;
    }
}
