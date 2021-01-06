package com.maciekbulanda.herokutest.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = {"http://localhost:3000", "https://hdt-react.herokuapp.com"})
@RestController
public class LoginController {

    @GetMapping("/login")
    Mono<Void> login() {
        return Mono.empty();
    }
}
