package com.neo4j.example.springdataneo4jintroapp.config;

import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class CustomNeo4jDriver {
    public static void main(String... args) {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
             Session session = driver.session()) {
            String message = "Hello, World!";
            String output = session.writeTransaction(tx -> {
                Result result = tx.run("CREATE (a:Greeting) " +
                                "SET a.message = $message " +
                                "RETURN a.message + ', from node ' + id(a)",
                        parameters("message", message));
                return result.single().get(0).asString();
            });
            System.out.println(output);
        }
    }
}
