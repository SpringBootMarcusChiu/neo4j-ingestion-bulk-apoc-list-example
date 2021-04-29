package com.neo4j.example.springdataneo4jintroapp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;

public class CustomStringSerializer extends StdScalarSerializer<String> {

    protected CustomStringSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.replaceAll("'","\\\\'"));
    }
}