package com.neo4j.example.springdataneo4jintroapp.repository;

import com.neo4j.example.springdataneo4jintroapp.model.ApplicationModule;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationModuleRepository extends Neo4jRepository<ApplicationModule, String> {
}
