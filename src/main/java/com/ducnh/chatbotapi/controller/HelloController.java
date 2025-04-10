package com.ducnh.chatbotapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {
    @GetMapping("/hello")
    public String getMethodName() {
        return "Hello ";
    }

    @GetMapping("/")
    public String index() {
        return "index ";
    }
}
