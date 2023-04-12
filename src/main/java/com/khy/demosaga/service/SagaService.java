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
    private static final String ORDER_REQUEST = "order-request";
    private static final String ORDER_RESPONSE = "order-response";
    private static final String DISCOUNT_REQUEST = "discount-request";
    private static final String DISCOUNT_RESPONSE = "discount-response";
    private static final String PAYMENT_REQUEST = "payment-request";
    private static final String PAYMENT_RESPONSE= "payment-response";
    private static final String ORDER_ID_HEADER = "orderId";
    private static SagaEvents sagaEvents;

    private final SagaProducer sagaProducer;

    private final StateMachineFactory<SagaStates, SagaEvents> factory;

    public Saga getNext(Saga saga) {
        SagaEvents nextEvent = getSagaEvent(SagaStates.valueOf(saga.currentState()));

        //
        SagaStates nextState = getNextState(getStateMachine(saga));
        log.info("nextStepString ========= {}", nextState);

        System.out.println(nextEvent);

        Saga nextSaga = Saga.builder()
                .customerId(saga.customerId())
                .orderId(saga.orderId())
                .eventTime(LocalDateTime.now())
                .currentState(String.valueOf(nextEvent))
                .value("data|aaa|bbb")
                .build();

        log.info("saga ::::: [{}]", saga.toString());
        log.info("next saga ::::: [{}]", nextSaga.toString());
        log.info("nextStep [0] = {}, [1] = {}", saga.currentState(), nextSaga.currentState());

        String topic = getTopic(nextEvent);

        // produce
        sagaProducer.asyncTest(topic, saga);

        return nextSaga;
    }

    private String getTopic(SagaEvents nextEvent) {
        return null;
    }

    private SagaEvents getSagaEvent(SagaStates state) {
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
            case PAYMENT_REQUEST_FAIL, PAYMENT_CANCEL_OK, PAYMENT_CANCELED -> {
                return sagaEvents.DISCOUNT_CANCEL;
            }
            case DISCOUNT_CHECK_FAIL, POINT_CHECK_FAIL, DISCOUNT_REQUEST_FAIL, DISCOUNT_CANCEL_OK -> {
                return sagaEvents.ORDER_CANCEL;
            }
            case PAYMENT_CANCEL_FAIL, DISCOUNT_CANCEL_FAIL -> {
                return sagaEvents.ORDER_CANCEL_FAIL;
            }
//            case ORDER_COMPLETED -> {
//                return sagaEvents.PAYMENT_CANCEL;
//            } // 고민이 필요해
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    public StateMachine<SagaStates, SagaEvents> getStateMachine(Saga saga) {
        StateMachine<SagaStates, SagaEvents> sm = this.build(saga);

        // get next event
        SagaEvents event = getSagaEvent(SagaStates.valueOf(saga.currentState()));
        log.info("get sagaEvent from switch case = {}", event);

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
                                                System.out.println("orderId = " + orderId);
                                            }));
                        }
                    });

                    sma.resetStateMachine(new DefaultStateMachineContext<>(
                            SagaStates.valueOf(saga.currentState()), null, null, null));
                });
        sm.start();
        return sm;
    }

    public static SagaStates getNextState(StateMachine<SagaStates, SagaEvents> stateMachine) {
        Transition<SagaStates, SagaEvents> transition = stateMachine.getTransitions().iterator().next();
        log.info("transition now status :: {}", transition.getTarget().getId().toString());
        return transition.getTarget().getId();
    }

}
