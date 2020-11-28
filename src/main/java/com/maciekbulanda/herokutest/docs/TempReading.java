package com.maciekbulanda.herokutest.docs;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class TempReading {
    private LocalDateTime readingtime;
}
