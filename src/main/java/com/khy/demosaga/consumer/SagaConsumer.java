package com.khy.demosaga.consumer;

import com.khy.demosaga.model.Saga;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SagaConsumer {

    private final String TOPICS = "saga-topic";

    @KafkaListener(id = "saga-listener-id", topics = TOPICS, containerFactory = "kafkaJsonContainerFactory")
    public void listenSaga(Saga saga) {
        log.info("Saga. saga=[{}]", saga);
        // todo
        // getNextStep()
    }
}
