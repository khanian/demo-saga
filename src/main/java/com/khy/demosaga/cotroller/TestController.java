package com.khy.demosaga.cotroller;

import com.khy.demosaga.dto.OrderSagaDto;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.service.SagaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.statemachine.StateMachine;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {

    private final SagaService sagaService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("hello")
    public String getHello() {
        return "Hello, World";
    }

    @GetMapping("v1/next")
    public Saga getNext() {
        Saga saga = Saga.builder()
                .orderId(1L)
                .customerId(1L)
                .productId(100L)
                .eventAt(LocalDateTime.now())
                .currentState(SagaStates.DISCOUNT_CHECK_OK)
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }

    @PostMapping("v1/nextPost")
    public Saga sendNext(@RequestBody OrderSagaDto sagaDto, Model model) {
        Saga saga = Saga.builder()
                .orderId(sagaDto.customerId())
                .customerId(sagaDto.customerId())
                .productId(sagaDto.productId())
                .amount(sagaDto.amount())
                .currentState(SagaStates.valueOf(sagaDto.currentState()))
                .eventAt(LocalDateTime.now())
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);
        return nextSaga;
    }

    @PostMapping("v1/responsePost")
    public Saga sendResponse(@RequestBody OrderSagaDto sagaDto) {
        Saga saga = modelMapper.map(sagaDto, Saga.class);
        Saga nextSaga = sagaService.sendResponse(saga);
        return nextSaga;
    }

    @GetMapping("v1/test")
    public List<Saga> testEvent() {
        List<Saga> sagaList = new ArrayList<>();

        Saga saga = Saga.builder()
                .eventAt(LocalDateTime.now())
                .customerId(2L)
                .orderId(2L)
                .productId(101L)
                .amount(2200L)
                .currentState(SagaStates.DISCOUNT_REQUEST_OK)
                .build();

        StateMachine<SagaStates, SagaEvents> sagaStateMachine = sagaService.getStateMachine(saga);
        log.info("after calling sagaStateMachine() : {}", sagaStateMachine.getState().getId().name());

        sagaList.add(saga);
        Saga nextSaga = Saga.builder()
                .eventAt(LocalDateTime.now())
                .customerId(saga.customerId())
                .orderId(saga.orderId())
                .amount(saga.amount())
                .currentState(sagaStateMachine.getState().getId())
                .build();
        sagaList.add(nextSaga);

        return sagaList;
    }
}
