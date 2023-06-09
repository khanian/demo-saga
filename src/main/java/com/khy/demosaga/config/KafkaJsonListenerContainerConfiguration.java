package com.khy.demosaga.config;

import com.khy.demosaga.model.Saga;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaJsonListenerContainerConfiguration {
//public class KafkaJsonListenerContainerConfiguration implements KafkaListenerConfigurer {
    //private final LocalValidatorFactoryBean validator;
    //public KafkaJsonListenerContainerConfiguration(LocalValidatorFactoryBean validator) {
//        this.validator = validator;
//    }
//    @Override
//    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
//        registrar.setValidator(validator);
//    }

//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
//        containerFactory.setConsumerFactory(consumerFactory());
//
//        return containerFactory;
//    }
//
//    private ConsumerFactory<String, String> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerProps());
//    }
//
//    private Map<String, Object> consumerProps() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, SagaConstants.BOOTSTRAP_SERVER);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return props;
//    }

    // json
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Saga>> kafkaJsonContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Saga> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ConsumerJsonFactory());
        return factory;
    }

    private ConsumerFactory<String, Saga> ConsumerJsonFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerJsonProps(),
                new StringDeserializer(),
                new JsonDeserializer<>(Saga.class)
        );
    }

    private Map<String, Object> consumerJsonProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, SagaConstants.BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }


}
