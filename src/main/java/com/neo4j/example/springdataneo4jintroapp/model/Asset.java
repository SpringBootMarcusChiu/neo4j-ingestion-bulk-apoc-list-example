package com.neo4j.example.springdataneo4jintroapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Transient;
import org.neo4j.ogm.id.UuidStrategy;

@Data
public class Asset {
    private Long id;
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = UuidStrategy.class)
    private String uuid;
    @Transient
    @EqualsAndHashCode.Include
    private String name;
}
