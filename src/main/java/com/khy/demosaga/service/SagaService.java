package com.khy.demosaga.service;

import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.producer.SagaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SagaService {

    private static final String SAGA_TOPIC = "saga-topic";
    private static final String ORDER_REQUEST = "order-request-v1";
    private static final String ORDER_RESPONSE = "order-response-v1";
    private static final String DISCOUNT_REQUEST = "discount-request-v1";
    private static final String DISCOUNT_RESPONSE = "discount-response-v1";
    private static final String PAYMENT_REQUEST = "payment-request-v1";
    private static final String PAYMENT_RESPONSE= "payment-response-v1";
    private static final String ORDER_ID_HEADER = "orderId";
    private static SagaEvents sagaEvents;

    private final SagaProducer sagaProducer;

    private final StateMachineFactory<SagaStates, SagaEvents> factory;

    public Saga getNextStep(Saga saga) {
        log.info("get Next step 1 = :::: {}", saga.currentState().toString());
        //StateMachine<SagaStates, SagaEvents> stateMachine = getStateMachine(saga);
        //SagaStates nextState = stateMachine.getState().getId();

        SagaStates nextState = getStateMachine(saga).getState().getId();

        log.info("next :::::: state = {}", nextState);
        System.out.println(nextState);

        Saga nextSaga = Saga.builder()
                .customerId(saga.customerId())
                .orderId(saga.orderId())
                .eventTime(LocalDateTime.now())
                .currentState(nextState)
                .value("data|aaa|bbb")
                .build();

        log.info("saga ::::: [{}]", saga.toString());
        log.info("next saga ::::: [{}]", nextSaga.toString());
        log.info("nextStep [0] = {}, [1] = {}", saga.currentState(), nextSaga.currentState());

        // produce
        String topic = getTopic(nextState);
        sagaProducer.send(topic, nextSaga);

        // insert nosql

        return nextSaga;
    }

    private String getTopic(SagaStates sagaStates) throws IllegalStateException {
        switch(sagaStates) {
            case DISCOUNT_CHECK, DISCOUNT_REQUEST, DISCOUNT_CANCEL -> {
                return DISCOUNT_REQUEST;
            }
            case POINT_CHECK, PAYMENT_REQUEST, PAYMENT_CANCEL -> {
                return PAYMENT_REQUEST;
            }
            case ORDER_CANCEL_FAIL, ORDER_CANCEL, ORDER_COMPLETE, ORDER_CANCEL_REQUEST -> {
                return ORDER_RESPONSE;
            }
            case ORDER_REQUEST -> {
                //return ORDER_REQUEST; // order request 에 보낼 일은 없는데...
                return ""; // topic 명 보내지 않음.
            } 
            default -> throw new IllegalStateException("Unexpected value: " + sagaStates);
        }
    }

    public SagaEvents getSagaEvent(SagaStates state) {
        log.info(":::::switch state {}", state.toString());
        switch(state) {
            case ORDER_REQUEST -> {
                return sagaEvents.DISCOUNT_CHECK;
            }
            case DISCOUNT_CHECK_OK -> {
                return sagaEvents.POINT_CHECK;
            }
            case POINT_CHECK_OK -> {
                return sagaEvents.DISCOUNT_REQUEST;
            }
            case DISCOUNT_REQUEST_OK -> {
                return sagaEvents.PAYMENT_REQUEST;
            }
            case PAYMENT_REQUEST_OK -> {
                return sagaEvents.ORDER_COMPLETE;
            }
            case PAYMENT_REQUEST_FAIL, PAYMENT_CANCEL_OK, PAYMENT_CANCEL -> {
                return sagaEvents.DISCOUNT_CANCEL;
            }
            case DISCOUNT_CHECK_FAIL, POINT_CHECK_FAIL, DISCOUNT_REQUEST_FAIL, DISCOUNT_CANCEL_OK -> {
                return sagaEvents.ORDER_CANCEL;
            }
            case PAYMENT_CANCEL_FAIL, DISCOUNT_CANCEL_FAIL, ORDER_CANCEL_FAIL -> {
                return sagaEvents.ORDER_CANCEL_FAIL;
            }
            case ORDER_CANCEL_REQUEST -> {
                return sagaEvents.PAYMENT_CANCEL;
            }
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    public StateMachine<SagaStates, SagaEvents> getStateMachine(Saga saga) {
        StateMachine<SagaStates, SagaEvents> sm = this.build(saga);

        // get next event
        SagaEvents event = getSagaEvent(saga.currentState());
        log.info("getStateMachine sagaEvent from switch case = {}", event);

        Message<SagaEvents> eventMessage = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, saga.orderId())
                .build();

        sm.sendEvent(eventMessage);
        return sm;
    }

    private StateMachine<SagaStates, SagaEvents> build(Saga saga) {
        StateMachine<SagaStates, SagaEvents> sm = this.factory.getStateMachine(String.valueOf(saga.orderId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
                        @Override
                        public void preStateChange(State<SagaStates, SagaEvents> state, Message<SagaEvents> message, Transition<SagaStates, SagaEvents> transition, StateMachine<SagaStates, SagaEvents> stateMachine, StateMachine<SagaStates, SagaEvents> rootStateMachine) {
                            Optional.ofNullable(message).ifPresent(msg ->
                                    Optional.ofNullable((Long)msg.getHeaders().getOrDefault(ORDER_ID_HEADER, -1L))
                                            .ifPresent(orderId -> {
                                                System.out.println("stateMachine build. ::: orderId = " + orderId);
                                            }));
                        }
                    });

                    sma.resetStateMachine(new DefaultStateMachineContext<>(
                            SagaStates.valueOf(saga.currentState().name()), null, null, null));
                });
        sm.start();
        return sm;
    }
}
