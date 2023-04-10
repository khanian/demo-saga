package com.khy.demosaga.cotroller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {
    @GetMapping("hello")
    public String getHello() {
        return "Hello, World";
    }

}
