package com.neo4j.example.springdataneo4jintroapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@EqualsAndHashCode(callSuper = true)
@Data
@RelationshipEntity("REL_1")
public class Relationship1 extends Asset {
    @StartNode
    private ApplicationModule applicationModule;
    @EndNode
    private Api api;
}
