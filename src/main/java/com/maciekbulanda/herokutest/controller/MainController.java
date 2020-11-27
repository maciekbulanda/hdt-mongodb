package com.maciekbulanda.herokutest.controller;

import com.maciekbulanda.herokutest.dto.InfoDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MainController {

    @GetMapping("/")
    Mono<InfoDto> displayInfo() {
        return Mono.just(new InfoDto());
    }
}
