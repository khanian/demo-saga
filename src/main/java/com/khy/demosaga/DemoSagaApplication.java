package com.khy.demosaga;

import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.producer.SagaProducer;
import com.khy.demosaga.service.SagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;

import java.time.LocalDateTime;
import java.util.UUID;


@SpringBootApplication
public class DemoSagaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSagaApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner (SagaProducer sagaProducer) {
        return args -> {
            Saga saga = Saga.builder()
                    .eventTime(LocalDateTime.now())
                    .customerId(1L)
                    .orderId(1L)
                    .currentState(String.valueOf(SagaStates.ORDER_REQUEST))
                    .value("")
                    .build();
            sagaProducer.async("saga-topic", saga);
        };
    }
}
