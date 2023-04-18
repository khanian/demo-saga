package com.khy.demosaga.producer;

import com.khy.demosaga.model.Saga;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;


@Slf4j
@Service
@AllArgsConstructor
public class SagaProducer {

    private final KafkaTemplate<String, Saga> kafkaJsonTemplate;

    public void send(String topic, Long customerId, Saga saga) {
        this.async(topic, customerId, saga);
    }

    private void async(String topic, Long customerId, Saga saga) {
        ListenableFuture<SendResult<String, Saga>> future = kafkaJsonTemplate.send(topic, String.valueOf(customerId), saga);
        future.addCallback(new KafkaSendCallback<>(){
            @Override
            public void onFailure(KafkaProducerException ex) {
                ProducerRecord<Object, Object> record = ex.getFailedProducerRecord();
                log.error("Fail to send Message. topic={}, record = {}", topic, record);
                // todo
                // batch or manual
            }

            @Override
            public void onSuccess(SendResult<String, Saga> result) {
                // modify debug
                log.info("Success to send message. topic={}. message = {}", topic, saga);
            }
        });
    }
}
