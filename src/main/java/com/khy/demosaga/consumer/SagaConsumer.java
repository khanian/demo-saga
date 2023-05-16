package com.khy.demosaga.consumer;

import com.khy.demosaga.config.SagaConstants;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.service.SagaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SagaConsumer {

    private final SagaService sagaService;

    @KafkaListener(id = SagaConstants.SAGA_STATE_TOPIC_ID, topics = SagaConstants.SAGA_STATE_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenSaga(Saga saga) {
        log.info("Only READ Saga Status History. saga={}", saga);
    }
    @KafkaListener(id = SagaConstants.SAGA_REQUEST_TOPIC_ID, topics = SagaConstants.SAGA_REQUEST_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenOrderRequest(Saga saga) {
        log.info(">>> Consume ORDER Request Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }
    @KafkaListener(id = SagaConstants.DISCOUNT_RESPONSE_TOPIC_ID, topics = SagaConstants.DISCOUNT_RESPONSE_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenDiscountResponse(Saga saga) {
        log.info("DISCOUNT Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }
    @KafkaListener(id = SagaConstants.PAYMENT_RESPONSE_TOPIC_ID, topics = SagaConstants.PAYMENT_RESPONSE_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenPaymentResponse(Saga saga) {
        log.info("PAYMENT Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }

}
