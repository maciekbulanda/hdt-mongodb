package com.maciekbulanda.herokutest.controller;

import com.maciekbulanda.herokutest.docs.TempReading;
import com.maciekbulanda.herokutest.dto.InfoDto;
import com.maciekbulanda.herokutest.repository.TempReadingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MainController {

    private final TempReadingRepository tempReadingRepository;

    public MainController(TempReadingRepository tempReadingRepository) {
        this.tempReadingRepository = tempReadingRepository;
    }

    @GetMapping("/")
    Mono<InfoDto> displayInfo() {
        return Mono.just(new InfoDto());
    }

    @PostMapping("/api/tempreading")
    Mono<TempReading> add(@RequestBody TempReading reading) {
        return tempReadingRepository.save(reading);
    }

}
