package com.khy.demosaga.consumer;

import com.khy.demosaga.config.SagaConstants;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.service.OtherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OtherConsumer {

    private final OtherService otherService;

    @KafkaListener(id = SagaConstants.DISCOUNT_REQUEST_TOPIC_ID, topics = SagaConstants.DISCOUNT_REQUEST_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenDiscountRequest(Saga saga) {
        log.info("Discount Request consume Saga. saga={}", saga);
        otherService.getAction(saga, SagaConstants.DISCOUNT_REQUEST_TOPIC);
    }

    @KafkaListener(id = SagaConstants.PAYMENT_REQUEST_TOPIC_ID, topics = SagaConstants.PAYMENT_REQUEST_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenPaymentRequest(Saga saga) {
        log.info("Payment Request consume Saga. saga={}", saga);
        otherService.getAction(saga, SagaConstants.PAYMENT_REQUEST_TOPIC);
    }

    @KafkaListener(id = SagaConstants.SAGA_RESPONSE_TOPIC_ID, topics = SagaConstants.SAGA_RESPONSE_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenOrderResponse(Saga saga) {
        log.info("Saga Response consume Saga. saga={}", saga);
        otherService.consumeSagaResponse(saga);
    }
}
