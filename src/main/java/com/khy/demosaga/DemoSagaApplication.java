package com.khy.demosaga;

import com.khy.demosaga.producer.SagaProducer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DemoSagaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSagaApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner (SagaProducer sagaProducer) {
        return args -> {
//            Saga saga = Saga.builder()
//                    .eventTime(LocalDateTime.now())
//                    .customerId(1L)
//                    .orderId(1L)
//                    .currentState(SagaStates.DISCOUNT_CHECK_OK)
//                    .value("")
//                    .build();
//            sagaProducer.send("saga-topic", saga.getCustomerId(), saga);
        };
    }
}
