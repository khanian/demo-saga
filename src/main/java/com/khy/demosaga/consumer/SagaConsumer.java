package com.khy.demosaga.consumer;

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
    private static final String SAGA_TOPIC = "saga-topic";
    private static final String ORDER_REQUEST = "order-request-v1";
    private static final String ORDER_RESPONSE = "order-response-v1";
    private static final String DISCOUNT_REQUEST = "discount-request-v1";
    private static final String DISCOUNT_RESPONSE = "discount-response-v1";
    private static final String PAYMENT_REQUEST = "payment-request-v1";
    private static final String PAYMENT_RESPONSE= "payment-response-v1";

    @KafkaListener(id = "saga-listener-id", topics = SAGA_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenSaga(Saga saga) {
        log.info("Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }
    @KafkaListener(id = "order-listener-id", topics = ORDER_REQUEST, containerFactory = "kafkaJsonContainerFactory")
    public void listenOrderRequest(Saga saga) {
        log.info("ORDER Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }
    @KafkaListener(id = "discount-listener-id", topics = DISCOUNT_RESPONSE, containerFactory = "kafkaJsonContainerFactory")
    public void listenDiscountResponse(Saga saga) {
        log.info("DISCOUNT Saga. saga=[{}]", saga);
        sendNextState(saga);
    }
    @KafkaListener(id = "payment-listener-id", topics = PAYMENT_RESPONSE, containerFactory = "kafkaJsonContainerFactory")
    public void listenPaymentResponse(Saga saga) {
        log.info("PAYMENT Saga. saga=[{}]", saga);
        sendNextState(saga);
    }

    private void sendNextState(Saga saga) {
        sagaService.getNextStep(saga);
    }
}
