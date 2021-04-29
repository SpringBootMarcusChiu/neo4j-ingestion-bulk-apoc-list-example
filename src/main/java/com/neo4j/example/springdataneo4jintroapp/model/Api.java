package com.neo4j.example.springdataneo4jintroapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@NodeEntity
public class Api extends Asset {
    @Relationship(type = "REL_1", direction = INCOMING)
    private List<Relationship1> relationship1List = new ArrayList<>();
    @Relationship(type = "REL_2", direction = INCOMING)
    private List<Relationship2> relationship2List = new ArrayList<>();
}
