package com.neo4j.example.springdataneo4jintroapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.neo4j.example.springdataneo4jintroapp.model.Application;
import lombok.Data;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(Application.class.getSimpleName());
        CsvMapper mapper = new CsvMapper()
                .configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS,true);

        CsvSchema schema = mapper.schemaFor(Pojo.class)
                .withNullValue("null")
                .withQuoteChar('\'')
                .withHeader();

        ObjectWriter writer = mapper.writer(schema);
        Pojo pojo = new Pojo();
        pojo.setFirstName("null");
        pojo.setLastName(null);
        String csv = writer.writeValueAsString(pojo);
        System.out.println(csv);
    }

    @Data
    public static class Pojo {
        private String firstName;
        private String lastName;
    }
}
