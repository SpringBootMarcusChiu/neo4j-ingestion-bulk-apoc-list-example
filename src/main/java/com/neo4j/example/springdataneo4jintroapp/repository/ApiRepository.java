package com.neo4j.example.springdataneo4jintroapp.repository;

import com.neo4j.example.springdataneo4jintroapp.model.Api;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRepository extends Neo4jRepository<Api, String> {
}
