package com.maciekbulanda.herokutest.controller;

import com.maciekbulanda.herokutest.docs.TempReading;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

//@WebFluxTest(controllers = MainController.class)
//@ContextConfiguration(classes = EmbeddedMongoAutoConfiguration.class)
//@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@AutoConfigureWebMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void add() {
        TempReading temp = new TempReading();
        temp.setReadingTime(LocalDateTime.now());
        temp.setReadingValue(25.1f);

        temp = webTestClient
                .post()
                .uri("http://localhost:8080/api/tempreading")
                .bodyValue(temp)
                .exchange()
                .expectStatus().isOk()
                .returnResult(TempReading.class)
                .getResponseBody().blockFirst();
        assertNotNull(temp.getId());
    }
}