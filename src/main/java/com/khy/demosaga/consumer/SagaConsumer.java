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

    @KafkaListener(id = "saga-listener-id", topics = SagaConstants.SAGA_STATE_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenSaga(Saga saga) {
        log.info("Only READ Saga Status History. saga=[{}]", saga);
        //sagaService.saveOrderSagaHistory(saga);
    }
    @KafkaListener(id = "order-listener-id", topics = SagaConstants.ORDER_REQUEST_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenOrderRequest(Saga saga) {
        log.info("ORDER Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }
    @KafkaListener(id = "discount-listener-id", topics = SagaConstants.DISCOUNT_RESPONSE_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenDiscountResponse(Saga saga) {
        log.info("DISCOUNT Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }
    @KafkaListener(id = "payment-listener-id", topics = SagaConstants.PAYMENT_RESPONSE_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenPaymentResponse(Saga saga) {
        log.info("PAYMENT Saga. saga=[{}]", saga);
        sagaService.getNextStep(saga);
    }

}
