package com.maciekbulanda.herokutest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LoginController {

    @GetMapping("/login")
    Mono<Void> login() {
        return Mono.empty();
    }
}
