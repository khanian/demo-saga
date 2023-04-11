package com.khy.demosaga.cotroller;

import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.service.SagaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    public List<Saga> getNext() {
        Saga saga = Saga.builder()
                .eventTime(LocalDateTime.now())
                .customerId(2L)
                .orderId(2L)
                .currentState(String.valueOf(SagaStates.PAYMENT_CANCEL_REQUEST))
                .value("")
                .build();
        return sagaService.getNext(saga);
    }
}
