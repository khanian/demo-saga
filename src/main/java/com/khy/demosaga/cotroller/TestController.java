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
        return "Hello, Kafka saga World";
    }

    @GetMapping("v1/next")
    public Saga getNext() {
        Saga saga = Saga.builder()
                .orderId(1L)
                .customerId(1L)
                .productId(100L)
                .eventAt(LocalDateTime.now())
                .currentState(String.valueOf(SagaStates.DISCOUNT_CHECK_OK))
                .build();
        Saga nextSaga = sagaService.getNextStep(saga);

        return nextSaga;
    }

    @PostMapping("v1/nextPost")
    public Saga sendNext(@RequestBody OrderSagaDto sagaDto, Model model) {
        Saga saga = modelMapper.map(sagaDto, Saga.class);
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
                .customerId(2L)
                .orderId(2L)
                .productId(101L)
                .amount(2200L)
                .shippingAddress("test")
                .currentState(String.valueOf(SagaStates.DISCOUNT_REQUEST_OK))
                .eventAt(LocalDateTime.now())
                .build();

        StateMachine<SagaStates, SagaEvents> sagaStateMachine = sagaService.getStateMachine(saga);
        log.info(">>> test after calling sagaStateMachine() : {}", sagaStateMachine.getState().getId().name());

        sagaList.add(saga);
        Saga nextSaga = Saga.builder()
                .customerId(saga.getCustomerId())
                .orderId(saga.getOrderId())
                .productId(saga.getProductId())
                .amount(saga.getAmount())
                .shippingAddress(saga.getShippingAddress())
                .currentState(String.valueOf(sagaStateMachine.getState().getId()))
                .eventAt(LocalDateTime.now())
                .build();
        sagaList.add(nextSaga);

        return sagaList;
    }
}
