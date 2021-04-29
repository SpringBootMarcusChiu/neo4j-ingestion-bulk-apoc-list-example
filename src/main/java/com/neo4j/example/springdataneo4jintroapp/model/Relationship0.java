package com.neo4j.example.springdataneo4jintroapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@EqualsAndHashCode(callSuper = true)
@Data
@RelationshipEntity("REL_0")
public class Relationship0 extends Asset {

    @StartNode
    private Application application;
    @EndNode
    private ApplicationModule applicationModule;
}
