package com.neo4j.example.springdataneo4jintroapp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Lists;
import com.neo4j.example.springdataneo4jintroapp.model.*;
import com.neo4j.example.springdataneo4jintroapp.repository.ApiRepository;
import com.neo4j.example.springdataneo4jintroapp.repository.ApplicationModuleRepository;
import com.neo4j.example.springdataneo4jintroapp.repository.ApplicationRepository;
import org.neo4j.ogm.annotation.Transient;
import org.neo4j.ogm.context.EntityGraphMapper;
import org.neo4j.ogm.cypher.compiler.CompileContext;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Neo4jSession;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringDataNeo4jIntroAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataNeo4jIntroAppApplication.class, args);
	}

	@Autowired
	ApplicationRepository applicationRepository;
	@Autowired
	ApplicationModuleRepository applicationModuleRepository;
	@Autowired
	ApiRepository apiRepository;
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public void run(String... args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
//		test0();
//		test1();
//		test3();
//		test4();
		test5();
		System.exit(0);
	}

	private void test5() throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Application app1 = new Application();
		app1.setName("Marcus,''Chiu");
		app1.setUuid("uuid,");
		Application app2 = new Application();
		app2.setTest("test");
		app2.setId(12L);
		app2.setUuid("something");
		List<Application> list = Arrays.asList(app1, app2);
		Class clazz = Application.class;
		Integer batchSize = 10000;
		Boolean parallel = true;

		System.out.println(genValues(clazz, list));
		Session session = sessionFactory.openSession();
		String query = "CALL apoc.periodic.iterate(" +
				"\"UNWIND [" + genValues(clazz, list) + "] AS line RETURN line\"," +
				"\"WITH apoc.map.fromLists([" + genHeaders(clazz) + "],line) AS map MERGE (n:" + genClass(clazz) + " {uuid: map.uuid}) SET n += apoc.map.clean(map,[],[''])\"," +
				"{batchSize:" + batchSize + ", parallel:" + parallel + "})";
		System.out.println(query);
		session.query(query, new HashMap<>());
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
//		mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

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

	public ObjectMapper configure(ObjectMapper mapper) {
		// configure mapper instance if required
//		mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
		// register default
//		mapper.findAndRegisterModules();
		// register custom
//		mapper.registerModule(createCustomModule());
		// ignore null fields
//		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		return mapper;
	}

	private SimpleModule createCustomModule() {
		SimpleModule module = new SimpleModule("CustomNull");
		// 1 - Add Serializer
		module.addSerializer(Application.class, new StdSerializer<>(Application.class) {
			@Override
			public void serialize(Application s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("uuid", s.getUuid());
				jsonGenerator.writeStringField("name", "null");
				jsonGenerator.writeEndObject();
			}
		});
		// 2 - Add Deserializer
//		module.addDeserializer(String.class, new StdDeserializer<>(String.class) {
//			@Override
//			public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//				String result = StringDeserializer.instance.deserialize(p, ctxt);
//				if (StringUtils.isEmpty(result)) {
//					return null;
//				}
//				return result;
//			}
//		});
		return module;
	}

	private void test4() {
		List<Class> supportedTypes = Arrays.asList(String.class, Number.class);
		Set<Field> set = new HashSet<>();

		Class clazz = Application.class;
		do {
			Field[] fields = clazz.getDeclaredFields();
			set.addAll(Arrays.asList(fields));
			clazz = clazz.getSuperclass();
		} while (clazz != null);

		set = set.stream()
				.filter(f -> f.getAnnotation(Transient.class) == null)
				.filter(f -> supportedTypes.contains(f.getType()))
				.collect(Collectors.toSet());

		set.forEach(f -> System.out.println(f.getName()));
	}

	private void test3() {
		Neo4jSession session = (Neo4jSession) sessionFactory.openSession();
		var entityGraphMapper = new EntityGraphMapper(session.metaData(), session.context());
		var app = new Application();
		app.setName("marcus");
		CompileContext c = entityGraphMapper.map(app, 0);
	}

	private void test1() {
		sessionFactory.openSession().save(new Application());
		Result r = sessionFactory.openSession().query("CREATE (n:Person)", new HashMap<>());
	}

	private void test0() {
		Application app = new Application(); app.setName("app");
		ApplicationModule am1 = new ApplicationModule(); am1.setName("am1");
		ApplicationModule am2 = new ApplicationModule(); am2.setName("am2");
		Api api = new Api(); api.setName("api");

		Relationship0 r01 = new Relationship0(); app.getRelationship0List().add(r01); r01.setApplication(app); r01.setApplicationModule(am1);
		Relationship0 r02 = new Relationship0(); app.getRelationship0List().add(r02); r02.setApplication(app); r02.setApplicationModule(am2);

		Relationship1 r11 = new Relationship1(); am1.getRelationship1List().add(r11); r11.setApplicationModule(am1); r11.setApi(api);
		Relationship1 r12 = new Relationship1(); am2.getRelationship1List().add(r12); r12.setApplicationModule(am2); r12.setApi(api);

		applicationRepository.save(app, 1);

		List<Application> apps = Lists.newArrayList(applicationRepository.findAll(1));
		List<Api> apis = Lists.newArrayList(apiRepository.findAll(2));
	}
}
