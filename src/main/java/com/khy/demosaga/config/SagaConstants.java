package com.khy.demosaga.config;

public class SagaConstants {
    //local
    //public static final String BOOTSTRAP_SERVER = "localhost:9092";
    // docker : localhost setting 127.0.0.1 kafka1 kafka2 kafka3
    public static final String BOOTSTRAP_SERVER = "kafka1:19092,kafka2:29092,kafka3:39092";
    public static final String SAGA_STATE_TOPIC = "saga-state-topic-v1";
    public static final String ORDER_REQUEST_TOPIC = "order-request-v1";
    public static final String ORDER_RESPONSE_TOPIC = "order-response-v1";
    public static final String DISCOUNT_REQUEST_TOPIC = "discount-request-v1";
    public static final String DISCOUNT_RESPONSE_TOPIC = "discount-response-v1";
    public static final String PAYMENT_REQUEST_TOPIC = "payment-request-v1";
    public static final String PAYMENT_RESPONSE_TOPIC= "payment-response-v1";
    public static final String ORDER_ID_HEADER = "orderId";
}
