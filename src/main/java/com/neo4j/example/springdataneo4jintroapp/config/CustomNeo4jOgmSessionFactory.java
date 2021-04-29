package com.neo4j.example.springdataneo4jintroapp.config;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import java.util.HashMap;

public class CustomNeo4jOgmSessionFactory {

    private static final Configuration configuration = new Configuration.Builder()
            .uri("bolt://localhost")
            .credentials("neo4j", "password")
//            .autoIndex("assert")
//            .generatedIndexesOutputDir("dir")
//            .generatedIndexesOutputFilename("filename")
//            .connectionPoolSize(200)
//            .encryptionLevel("REQUIRED")
//            .trustStrategy("TRUST_SIGNED_CERTIFICATES")
//            .trustCertFile("/tmp/cert")
//            .connectionLivenessCheckTimeout(1000)
            .build();

    public static void main(String[] args) {
        var sessionFactory = new SessionFactory(configuration,
                "com.neo4j.example.springdataneo4jintroapp.model");
        var session = sessionFactory.openSession();
        var result = session.query("CREATE (n) RETURN n", new HashMap<>());
        System.out.println(result.toString());
    }
}
