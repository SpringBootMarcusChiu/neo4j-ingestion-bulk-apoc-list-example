package com.neo4j.example.springdataneo4jintroapp.repository;

import com.neo4j.example.springdataneo4jintroapp.model.Application;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends Neo4jRepository<Application, String> {
}
