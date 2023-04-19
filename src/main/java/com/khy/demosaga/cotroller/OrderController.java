package com.khy.demosaga.cotroller;

import com.khy.demosaga.dto.OrderSagaDto;
import com.khy.demosaga.model.Saga;
import com.khy.demosaga.service.SagaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Slf4j
@Controller
@AllArgsConstructor
public class OrderController {

    private final SagaService sagaService;
    private final ModelMapper modelMapper = new ModelMapper();
    @GetMapping("orderForm")
    public String checkoutForm(Model moder) {
        log.info(">>> orderForm .....");
        return "orderForm";
    }

    @PostMapping("v1/submitOrder")
    public String submitOrder(OrderSagaDto sagaDto, Model model) {
        sagaDto.setEventAt(LocalDateTime.now());
        log.info(">>> sagaDto = {} ", sagaDto.toString());
        Saga saga = modelMapper.map(sagaDto, Saga.class);
        log.info(">>> saga = {} ", saga.toString());
        Saga nextSaga = sagaService.getNextStep(saga);
        model.addAttribute("orderSaga", nextSaga);
        return "submitComplete";
    }
}
