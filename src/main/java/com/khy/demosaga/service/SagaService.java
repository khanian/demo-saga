package com.khy.demosaga.service;

import com.khy.demosaga.config.SagaConstants;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import com.khy.demosaga.producer.SagaProducer;
import com.khy.demosaga.repository.OrderSagaEntity;
import com.khy.demosaga.repository.OrderSagaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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

    private static SagaEvents sagaEvents;

    private final SagaProducer sagaProducer;

    private final StateMachineFactory<SagaStates, SagaEvents> factory;

    private final ModelMapper modelMapper = new ModelMapper();

    private final OrderSagaRepository orderSagaRepository;

    public Saga getNextStep(Saga saga) {
        log.info("get Next step 1 = :::: {}", saga.getCurrentState().toString());
        sendStateLog(saga);
        //StateMachine<SagaStates, SagaEvents> stateMachine = getStateMachine(saga);
        //SagaStates nextState = stateMachine.getState().getId();

        SagaStates nextState = getStateMachine(saga).getState().getId();

        log.info("next :::::: state = {}", nextState);
        System.out.println(nextState);

        Saga nextSaga = Saga.builder()
                .customerId(saga.getCustomerId())
                .orderId(saga.getOrderId())
                .productId(saga.getProductId())
                .amount(saga.getAmount())
                .shippingAddress(saga.getShippingAddress())
                .currentState(String.valueOf(nextState))
                .eventAt(LocalDateTime.now())
                .build();

        log.info(">>> saga ::::: [{}]", saga.toString());
        log.info(">>> next saga ::::: [{}]", nextSaga.toString());
        log.info(">>> nextStep from {} to {}", saga.getCurrentState(), nextSaga.getCurrentState());

        // produce
        String topic = getProduceTopic(nextState);
        sagaProducer.send(topic, nextSaga.getCustomerId(), nextSaga);

        // save status insert nosql
        sendStateLog(nextSaga);
        return nextSaga;
    }

    private String getProduceTopic(SagaStates sagaStates) throws IllegalStateException {
        switch(sagaStates) {
            case DISCOUNT_CHECK, DISCOUNT_REQUEST, DISCOUNT_CANCEL -> {
                return SagaConstants.DISCOUNT_REQUEST_TOPIC;
            }
            case POINT_CHECK, PAYMENT_REQUEST, PAYMENT_CANCEL -> {
                return SagaConstants.PAYMENT_REQUEST_TOPIC;
            }
            case ORDER_CANCEL_FAIL, ORDER_CANCEL, ORDER_COMPLETE, ORDER_CANCEL_REQUEST -> {
                return SagaConstants.SAGA_RESPONSE_TOPIC;
            }
            default -> throw new IllegalStateException("Unexpected value: " + sagaStates);
        }
    }

    public SagaEvents getSagaEvent(SagaStates state) {
        log.info(":::::switch state {}", state.toString());
        switch(state) {
            case ORDER_REQUEST -> {
                return SagaEvents.DISCOUNT_CHECK;
            }
            case DISCOUNT_CHECK_OK -> {
                return SagaEvents.POINT_CHECK;
            }
            case POINT_CHECK_OK -> {
                return SagaEvents.DISCOUNT_REQUEST;
            }
            case DISCOUNT_REQUEST_OK -> {
                return SagaEvents.PAYMENT_REQUEST;
            }
            case PAYMENT_REQUEST_OK -> {
                return SagaEvents.ORDER_COMPLETE;
            }
            case PAYMENT_REQUEST_FAIL, PAYMENT_CANCEL_OK -> {
                return SagaEvents.DISCOUNT_CANCEL;
            }
            case DISCOUNT_CHECK_FAIL, POINT_CHECK_FAIL, DISCOUNT_REQUEST_FAIL, DISCOUNT_CANCEL_OK -> {
                return SagaEvents.ORDER_CANCEL;
            }
            case PAYMENT_CANCEL_FAIL, DISCOUNT_CANCEL_FAIL, ORDER_CANCEL_FAIL -> {
                return SagaEvents.ORDER_CANCEL_FAIL;
            }
            case ORDER_CANCEL_REQUEST, PAYMENT_CANCEL -> {
                return SagaEvents.PAYMENT_CANCEL;
            }
            default -> throw new IllegalStateException("[강]Unexpected state value in Event Method: " + state);
        }
    }

    public StateMachine<SagaStates, SagaEvents> getStateMachine(Saga saga) {
        StateMachine<SagaStates, SagaEvents> sm = this.build(saga);

        // get next event
        SagaEvents event = getSagaEvent(SagaStates.valueOf(saga.getCurrentState()));
        log.info("getStateMachine sagaEvent from switch case = {}", event);

        Message<SagaEvents> eventMessage = MessageBuilder.withPayload(event)
                .setHeader(SagaConstants.ORDER_ID_HEADER, saga.getOrderId())
                .build();

        sm.sendEvent(eventMessage);
        return sm;
    }

    private StateMachine<SagaStates, SagaEvents> build(Saga saga) {
        StateMachine<SagaStates, SagaEvents> sm = this.factory.getStateMachine(String.valueOf(saga.getOrderId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
                        @Override
                        public void preStateChange(State<SagaStates, SagaEvents> state, Message<SagaEvents> message, Transition<SagaStates, SagaEvents> transition, StateMachine<SagaStates, SagaEvents> stateMachine, StateMachine<SagaStates, SagaEvents> rootStateMachine) {
                            Optional.ofNullable(message).ifPresent(msg ->
                                    Optional.ofNullable((Long)msg.getHeaders().getOrDefault(SagaConstants.ORDER_ID_HEADER, -1L))
                                            .ifPresent(orderId -> {
                                                System.out.println("stateMachine build. ::: orderId = " + orderId);
                                            }));
                        }
                    });

                    sma.resetStateMachine(new DefaultStateMachineContext<>(
                            SagaStates.valueOf(saga.getCurrentState()), null, null, null));
                });
        sm.start();
        return sm;
    }

    private void sendStateLog(Saga saga) {
        // send state information for save state to nosql
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderSagaEntity orderSagaEntity = modelMapper.map(saga, OrderSagaEntity.class);
        log.info(">>> orderSagaEntity === {}", orderSagaEntity.toString());
        orderSagaRepository.save(orderSagaEntity);
        log.info(">>> send Stat log ::::::: {}, {}", saga.getCurrentState(), saga);
        sagaProducer.send(SagaConstants.SAGA_STATE_TOPIC, saga.getCustomerId(), saga);
    }

}
