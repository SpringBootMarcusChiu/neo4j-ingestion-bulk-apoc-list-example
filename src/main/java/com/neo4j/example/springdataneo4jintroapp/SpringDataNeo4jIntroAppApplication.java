package com.neo4j.example.springdataneo4jintroapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.neo4j.example.springdataneo4jintroapp.model.Application;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class SpringDataNeo4jIntroAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataNeo4jIntroAppApplication.class, args);
	}

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public void run(String... args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		List<Application> list = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			Application app1 = new Application();
			app1.setName("Marcus,''Chiu");
			app1.setTest("test");
			app1.setUuid("uuid," + i);
			list.add(app1);
		}
		Class clazz = Application.class;
		Integer batchSize = 100;
		Boolean parallel = true;

		Session session = sessionFactory.openSession();
		String query = "CALL apoc.periodic.iterate(" +
				"\"UNWIND [" + genValues(clazz, list) + "] AS line RETURN line\"," +
				"\"WITH apoc.map.fromLists([" + genHeaders(clazz) + "],line) AS map MERGE (n:" + genClass(clazz) + " {uuid: map.uuid}) SET n += apoc.map.clean(map,[],[''])\"," +
				"{batchSize:" + batchSize + ", parallel:" + parallel + "})";

		System.out.println("STARTED");
		Long start = System.currentTimeMillis();
		session.query(query, new HashMap<>());
		Long end = System.currentTimeMillis();

		System.out.println("time: " + (end - start));

		System.exit(0);
	}

	private String genClass(final Class clazz) {
		var labels = new StringBuilder(clazz.getSimpleName());
		var superClass = clazz.getSuperclass();
		while (!superClass.getSimpleName().equals("Object")) {
			labels.append(":").append(superClass.getSimpleName());
			superClass = superClass.getSuperclass();
		}
		return labels.toString();
	}

	private String genValues(Class clazz, List<Application> applications) throws IOException {
		CsvMapper mapper = new CsvMapper()
				.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS,true)
				.configure(CsvGenerator.Feature.ESCAPE_QUOTE_CHAR_WITH_ESCAPE_CHAR, true);
		SimpleModule module = new SimpleModule();
		module.addSerializer(String.class, new CustomStringSerializer(String.class));
		mapper.registerModule(module);

		CsvSchema schema = mapper.schemaFor(clazz)
				.withNullValue("null")
				.withQuoteChar('\'')
				.withEscapeChar('\\')
				.withColumnSeparator(',')

				.withLineSeparator("],[")
				.withoutHeader();
		ObjectWriter writer = mapper.writer(schema);

		var baos = new ByteArrayOutputStream();
		writer.writeValues(baos).writeAll(applications);
		String s = "[" + baos.toString(StandardCharsets.UTF_8);
		return s.substring(0, s.length() - 2);
	}

	private String genHeaders(Class clazz) throws NoSuchMethodException, JsonProcessingException, IllegalAccessException, InvocationTargetException, InstantiationException {
		CsvMapper mapper = new CsvMapper()
				.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS,true);
		CsvSchema schema = mapper.schemaFor(clazz)
				.withNullValue("null")
				.withQuoteChar('\'')
				.withEscapeChar('\\')
				.withColumnSeparator(',')

				.withHeader();
		ObjectWriter writer = mapper.writer(schema);
		var csv = writer.writeValueAsString(clazz.getConstructor().newInstance());
		String[] l = csv.split("\n", 2);
		return l[0];
	}
}
