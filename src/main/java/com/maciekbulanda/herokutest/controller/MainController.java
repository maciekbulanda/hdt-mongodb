package com.maciekbulanda.herokutest.controller;

import com.maciekbulanda.herokutest.docs.TempReading;
import com.maciekbulanda.herokutest.dto.InfoDto;
import com.maciekbulanda.herokutest.repository.TempReadingRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = {"http://localhost:3000", "https://hdt-react.herokuapp.com"})
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

    @GetMapping("/api/tempreading")
    Flux<TempReading> getReadings() {
        return tempReadingRepository.findAll();
    }

    @PostMapping("/api/tempreading")
    Mono<TempReading> addReading(@RequestBody TempReading reading) {
        return tempReadingRepository.save(reading);
    }

    @DeleteMapping("/api/tempreading")
    Mono<Void> deleteReading(@RequestParam String id) {
        return tempReadingRepository.deleteById(id);
    }

}
