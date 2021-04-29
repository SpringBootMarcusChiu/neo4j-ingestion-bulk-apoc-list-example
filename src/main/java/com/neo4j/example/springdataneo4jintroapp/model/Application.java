package com.neo4j.example.springdataneo4jintroapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@NodeEntity
public class Application extends Asset {
    private String test;
    @Transient
    @JsonIgnore
    @Relationship("REL_0")
    private List<Relationship0> relationship0List = new ArrayList<>();
}
