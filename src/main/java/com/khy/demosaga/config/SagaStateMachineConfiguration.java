package com.khy.demosaga.config;

import com.khy.demosaga.model.Saga;
import com.khy.demosaga.model.SagaEvents;
import com.khy.demosaga.model.SagaStates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;

import java.util.Optional;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
class SagaStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<SagaStates, SagaEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<SagaStates, SagaEvents> states) throws Exception {
        states
                .withStates()
                .initial(SagaStates.ORDER_REQUEST)
                .state(SagaStates.DISCOUNT_CHECKED)
                .state(SagaStates.DISCOUNT_CHECK_OK)
                .state(SagaStates.DISCOUNT_CHECK_FAIL)
                .state(SagaStates.POINT_CHECKED)
                .state(SagaStates.POINT_CHECK_OK)
                .state(SagaStates.POINT_CHECK_FAIL)
                .state(SagaStates.DISCOUNT_REQUESTED)
                .state(SagaStates.DISCOUNT_REQUEST_OK)
                .state(SagaStates.DISCOUNT_REQUEST_FAIL)
                .state(SagaStates.PAYMENT_REQUESTED)
                .state(SagaStates.PAYMENT_REQUEST_OK)
                .state(SagaStates.PAYMENT_REQUEST_FAIL)
                .state(SagaStates.PAYMENT_CANCELED)
                .state(SagaStates.PAYMENT_CANCEL_OK)
                .state(SagaStates.PAYMENT_CANCEL_FAIL)
                .state(SagaStates.DISCOUNT_CANCELED)
                .state(SagaStates.DISCOUNT_CANCEL_OK)
                .state(SagaStates.DISCOUNT_CANCEL_FAIL)
                .end(SagaStates.ORDER_CANCEL_FAIL)
                .end(SagaStates.ORDER_CANCELED)
                .end(SagaStates.ORDER_COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SagaStates, SagaEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(SagaStates.ORDER_REQUEST).target(SagaStates.DISCOUNT_CHECKED).event(SagaEvents.DISCOUNT_QUERY)
                .and()
                .withExternal()
                .source(SagaStates.DISCOUNT_CHECK_OK).target(SagaStates.POINT_CHECKED).event(SagaEvents.POINT_QUERY)
                .and()
                .withExternal()
                .source(SagaStates.DISCOUNT_CHECK_FAIL).target(SagaStates.ORDER_CANCELED).event(SagaEvents.ORDER_CANCEL)
                .and()
                .withExternal()
                .source(SagaStates.POINT_CHECK_OK).target(SagaStates.DISCOUNT_REQUESTED).event(SagaEvents.DISCOUNT_REQUEST)
                .and()
                .withExternal()
                .source(SagaStates.POINT_CHECK_FAIL).target(SagaStates.ORDER_CANCELED).event(SagaEvents.ORDER_CANCEL)
                .and()
                .withExternal()
                .source(SagaStates.DISCOUNT_REQUEST_OK).target(SagaStates.PAYMENT_REQUESTED).event(SagaEvents.PAYMENT_REQUEST)
                .and()
                .withExternal()
                .source(SagaStates.DISCOUNT_REQUEST_FAIL).target(SagaStates.ORDER_CANCELED).event(SagaEvents.ORDER_CANCEL)
                .and()
                .withExternal()
                .source(SagaStates.PAYMENT_REQUEST_OK).target(SagaStates.ORDER_COMPLETED).event(SagaEvents.ORDER_COMPLETE)
                .and()
                .withExternal()
                .source(SagaStates.PAYMENT_REQUEST_FAIL).target(SagaStates.DISCOUNT_CANCELED).event(SagaEvents.DISCOUNT_CANCEL)
                .and()
                .withExternal()
                .source(SagaStates.DISCOUNT_CANCEL_OK).target(SagaStates.ORDER_CANCELED).event(SagaEvents.ORDER_CANCEL)
                .and()
                .withExternal()
                .source(SagaStates.DISCOUNT_CANCEL_FAIL).target(SagaStates.ORDER_CANCEL_FAIL).event(SagaEvents.ORDER_CANCEL_ERROR)
                .and()
                .withExternal()
                .source(SagaStates.ORDER_COMPLETED).target(SagaStates.PAYMENT_CANCELED).event(SagaEvents.PAYMENT_CANCEL)
                .and()
                .withExternal()
                .source(SagaStates.PAYMENT_CANCEL_OK).target(SagaStates.DISCOUNT_CANCELED).event(SagaEvents.DISCOUNT_CANCEL)
                .and()
                .withExternal()
                .source(SagaStates.PAYMENT_CANCEL_FAIL).target(SagaStates.ORDER_CANCEL_FAIL).event(SagaEvents.ORDER_CANCEL_ERROR)
        ;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<SagaStates, SagaEvents> config) throws Exception {
        StateMachineListenerAdapter<SagaStates, SagaEvents> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<SagaStates, SagaEvents> fromState, State<SagaStates, SagaEvents> toState) {
                // 리스너의 동작을 구현
                log.info("State changed from {} to {}",
                        fromState == null ? "start" : fromState.getId().toString(),
                        toState.getId().toString());
            }
        };
        config.withConfiguration().listener(adapter); // 리스너를 등록
    }
}