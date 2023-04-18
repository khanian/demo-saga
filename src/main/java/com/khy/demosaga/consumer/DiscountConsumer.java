package com.khy.demosaga.consumer;

import com.khy.demosaga.config.SagaConstants;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.service.DiscountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class DiscountConsumer {

    private final DiscountService discountService;
    @KafkaListener(id = "discount-request-listener-id", topics = SagaConstants.DISCOUNT_REQUEST_TOPIC, containerFactory = "kafkaJsonContainerFactory")
    public void listenDiscountRequest(Saga saga) {
        log.info("Discount Request consume Saga. saga={}", saga);
        discountService.getAction(saga);
    }
}
