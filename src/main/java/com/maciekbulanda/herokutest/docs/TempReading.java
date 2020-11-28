package com.maciekbulanda.herokutest.docs;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "templogs")
public class TempReading {
    @Id
    private String id;
    private LocalDateTime readingTime;
    private Float readingValue;

    public TempReading() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }

    public Float getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(Float readingValue) {
        this.readingValue = readingValue;
    }
}
