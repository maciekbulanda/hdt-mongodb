package com.maciekbulanda.herokutest.repository;

import com.maciekbulanda.herokutest.docs.TempReading;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempReadingRepository extends ReactiveMongoRepository<TempReading, String> {
}
