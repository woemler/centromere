# Centromere documentation

#### [Introduction](#introduction)
- [About](#about)
- [Demo](#demo)
- [License](#license)
- [Requirements](#requirements)
- [Maven Artifacts](#maven-artifacts)

#### [Getting Started](#getting-started)
- [Data Models](#data-models)
- [Repositories](#repositories)
- [Data Import](#data-import)
- [Web Service Controllers](#web-service-controllers)

#### [REST API](#rest-api)

#### [Modules](#modules)
- [Centromere Core](#centromere-core)
  - [Customizing Model Classes](#customizing-model-classes)
- [Centromere MongoDB](#centromere-mongodb)
  - [MongoDB Repositories](#mongodb-repositories)
- [Centromere SQL](#centromere-sql)
  - [SQL Repositories](#sql-repositories)
- [Centromere JPA](#centromere-jpa)
  - [JPA Repositories](#jpa-repositores)
- [Centromere Data Import CLI](#centromere-data-import-cli)
  - [Import Configuration](#import-configuration)
  - [Running the Import](#running-the-import)
- [Centromere Web](#centromere-web)
  - [Web Service Configuration](#web-service-configuration)

#### [Getting Started](#getting-started)

# Introduction

## About

Centromere is a set of tools for developing scalable data warehouses and RESTful web services for processed genomic data.  It is designed to be modular and flexible, allowing you to mold it to fit your data model and business needs. Centromere is developed using the open-source, enterprise-grade Spring Framework, and supports integration with multiple database technologies.  You can use Centromere to create a new data warehouse from scratch, or bootstrap one or more existing databases, and make your data available via a customizable REST API.

Centromere aims to help solve some common problems inherent in monolithic bioinformatics app development:
- Fragmentation of data across large organizations.
- Inconsistent annotation.
- Horizontal and vertical scalability.
- Repetition of work in software development.

What Centromere is _**not**_:
- A LIMS system.
- A repository for raw genomic data.
- An analysis platform.
- An end-user GUI application.

## Demo

A demo implementation of a Centromere data warehouse and web API is available as a [GitHub repository](https://github.com/oncoblocks/centromere-demo).  This demo utilizes a small data set of cancer genomic data from the TCGA to showcase the features of Centromere's web service and data import utilities.  A hosted instance of this demo is coming soon.

## License

Centromere is licensed under the Apache License, version 2.0:

> Copyright 2016 William Oemler, Blueprint Medicines

> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Requirements

- Linux or OS X
- Java JDK 8+
- Maven 3
- MongoDB or SQL Database


## Maven Artifacts

Artifacts for Centromere release builds are available from the Maven Central Repository:

```xml
<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-core</artifactId>
    <version>0.4.0</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-data-import-cli</artifactId>
    <version>0.4.0</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-mongodb</artifactId>
    <version>0.4.0</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-sql</artifactId>
    <version>0.4.0</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-web</artifactId>
    <version>0.4.0</version>
</dependency>
```

# Getting Started

### Data Models

The core of every Centromere data warehouse are the data model classes, which are represented as implementations of the `Model` interface.  The entities serve as data transfer objects (DTOs) to move data between application layers, and define how data is queried and represented in the web services.  While models can themselves represent aggregate data or database views, they should be considered atomic entities when reading and serving data through the web API.  Each model will have its own data access object (DAO) repository class and REST endpoint controller class.

The `Model` interface looks like this:

```java
@Filterable
public interface Model<ID extends Serializable> {
	ID getId();
}
```

A basic `Model` implementation looks like a plain old java object (POJO):

```java
/* Simple database-agnostic model */
public class Gene implements Model<String> {

	private String id;
	private Long publicId;
	private String geneSymbol;
	private String species;
	private String chromosome;
	private Set<String> aliases;
	private Map<String,String> attributes;

	public String getId(){ return id; }

	/* Getters and Setters */
}
```

The `Model` interface defines three thing about all model classes:

- They must have an identifying attribute, which is intended to serve as the primary key ID (in this case, a `String` representation of a MongoDB `ObjectID`).
- This identifier must be a serializable object, for the sake of representation in a URL.
- The attributes in the object may be filtered in web service responses, using object transformers like Jackson.

Model object persistence will be handled depending on the database back-end and integration module being used (eg. `centromere-mongodb` or `centromere-jpa`).  For some implementations, the above `Model` example is all you need to get started, but sometimes additional customization is required to get the maximum utility:

```java
/* MongoDB model */
@Document(collection = "genes")
public class Gene implements Model<String> {

	@Id private String id;
	@Indexed private Long publicId;
	@Indexed private String geneSymbol;
	private String species;
	private String chromosome;
	private Set<String> aliases;
	private Map<String,String> attributes;

	public String getId(){ return id; }

	/* Getters and Setters */
}
```

### Repositories

All Centromere repository classes implement the base `RepositoryOperations` interface, which defines all of the basic CRUD operations that all Centromere repositories should support.  This interface is based on Spring Data's `PagingAndSortingRepository`, but with some additional method definitions for dynamic query operations.  The database-specific implementations also include several methods specific to those data stores.  A MongoDB repository implementation for the above `Gene` model class might look like this:

```java
@ModelRepository(Gene.class)
public class GeneRepository extends GenericMongoRepository<Gene, String> {
	@Autowired
	public GeneRepository(MongoTemplate mongoTemplate){
		super(mongoTemplate);
	}
}
```

Dynamic repository queries in Centromere are created by chaining a series of query operations, represented by the `QueryCriteria` class.  These operations are defined by the field to be queried, the value of the field, and the operator to be used to make the evaluation.  For example:

```java
QueryCriteria criteria = new QueryCriteria("publicId", 1L, Evaluation.EQUALS)

/*
Will be translated based upon the database implementation to:
   WHERE publicId = 1 # for SQL
   or
   {"publicId": 1} # for MongoDB
*/

```

### Data Import

The `centromere-core` module contains a number of simple classes intended to aid in the development of data import pipelines.  The core of this are four basic interfaces: `RecordReader`, `RecordWriter`, `RecordImporter`, and Spring's `Validator`.  When combined with a `RecordProcessor`, these components create a utility for importing a specific data type input into database records.  For example, components for importing Entrez Gene records into a MongoDB database might look like this:

```java
/* RecordReaders take an input data source and return Model objects.*/
public class GeneInfoReader extends AbstractRecordFileReader<Gene> {

	@Override
	public Gene readRecord() throws DataImportException {
		Gene gene = null;
		String line;
		try {
			boolean flag = true;
			while(flag) {
				line = this.getReader().readLine();
				if (line == null || !line.startsWith("#Format: tax_id GeneID")) {
					flag = false;
					if (line != null && !line.equals("")) gene = getRecordFromLine(line);
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		return gene;
	}

	private Gene getRecordFromLine(String line){
		String[] bits = line.split("\\t");
		Gene gene = new Gene();
		gene.setSpecies(Integer.parseInt(bits[0]));
		gene.setPublicId(Long.parseLong(bits[1]));
		gene.setGeneSymbol(bits[2]);
		gene.setAliases(new HashSet<>(Arrays.asList(bits[3].split("\\|"))));
		gene.setChromosome(bits[5]);
		return gene;
	}

}

/* Validators asses whether a Model object has been correctly constructed.*/
public class GeneValidator implements Validator {

	public boolean supports(Class<?> aClass) {
		return aClass.equals(Gene.class);
	}

	public void validate(Object o, Errors errors) {
		Gene gene = (Gene) o;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors,  "geneSymbol", "symbol.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors,  "publicId", "publicId.empty");
	}
}

/* RecordWriters take Model objects and write them to temporary files or directly to a database.*/
@Component
public class GeneRepositoryWriter extends RepositoryRecordWriter<Gene> {
	@Autowired
	public GeneRepositoryWriter(GeneRepository repository){
		super(repository);
	}
}

/* RecordProcessors tie all of these components together to import a specific data type. */
@Component
public class GeneInfoProcessor extends GenericRecordProcessor<Gene> {
	@Autowired
	public GeneInfoProcessor(GeneRepositoryWriter writer){
		super(new GeneInfoReader(), new GeneValidator(), writer);
	}
}
```

### Web Service Controllers

The web services controller layer handles HTTP requests and routes them to the appropriate repository implementation.  Web service controllers support standard CRUD operations via `GET`, `POST`, `PUT`, and `DELETE` methods. `HEAD` and `OPTIONS` methods are also supported for the purpose of exposing additional resource endpoint information.

```java
@Controller
@ExposesResourcesFor(Gene.class)
@RequestMapping(value = "/api/genes")
public class GeneController extends CrudApiController<Gene, String> {

	@Autowired
	public GeneController(GeneRepository repository, EntityLinks entityLinks) {
		super(repository, Gene.class,
		new ModelResourceAssembler(GeneController.class, Gene.class, entityLinks));
	}

}
```

# REST API

Once your application is up-and-running, you can reach your data using the relative root URLs specified in your controller classes.  For example:

Method | URI | Description
-------|-----|------------
`GET` | `/genes` | Fetches all Genes
`GET` | `/genes/{id}` | Fetch a single Gene by ID
`POST` | `/genes` | Creates a new Gene record
`PUT` | `/genes/{id}` | Updates an existing Gene
`DELETE` | `/genes/{id}` | Deletes an existing Gene
`OPTIONS` | `/genes` | Fetches info about the available Gene operations

#### Media Types

Centromere supports the following media types for `GET` requests: `application/json`, `application/hal+json`, `application/xml`, `application/hal+xml`, and `plain/text`.  If no return type is specified, `application/json` will be used by default.  The media type `plain/text` returns the response data as a tab-delimited text table.  Only requests for the media types `application/hal+json` and `application/hal+xml` will return HATEOAS links.

#### Searching

Centromere supports dynamic query operations using query string parameters.  The available query parameters for each resource are defined in the model class (see the documentation for the `centromere-core` module). You can perform queries using one or more entity attributes in the standard way:

```
GET /genes?alias=akt,mtor,braf
GET /cnv?valueBetween=-0.5,0.5
GET /samples?tissue=lung&type=cellLine
```

#### Paging and Sorting

Requests can return results that are both paginated and sorted.  Centromere uses the default Spring Data URI query parameter syntax:

```
GET /genes?size=100&page=2&sort=entrezGeneId,asc
```

Page numbering starts from zero, and the default page size is 1000 records.  If sorting by multiple fields, use multiple `sort` parameters in your request.  The sorts will be applied in the order given:

```
# Will first sort by `type`, then by `entrezGeneId`
GET /genes?sort=type,desc&sort=entrezGeneId,asc
```

#### Field Filtering

Requests can specify which entity fields will be returned, or excluded:

```
GET /genes?fields=entrezGeneId,primaryGeneSymbol
GET /genes?exclude=description,links
```

#### Hypermedia

For hypermedia support, use the `application/hal+json` or `application/hal+xml` media types to include embedded HAL-formatted links to related entities, allowing for easy resource discovery:

```
Request:
GET /hgu133/rma

Response:
200 OK
[
	{
		sampleId: 123,
		entrezGeneId: 207,
		dataSetId: 43,
		value: 103.12
		links: [
			{ rel: "self", href: "http://myapp/hgu133/rma?sampleId=123&entrezGeneId=207&dataSetId=43"  },
			{ rel: "sample", href: "http://myapp/samples/123"  },
			{ rel: "gene", href: "http://myapp/genes/207"  },
			{ rel: "data_file", href: "http://myapp/datafiles/43"  }
		]
	}, ...
]
```

#### Compression

All responses can be GZIP compressed by including the `Accept-Encoding: gzip,deflate` header:

```
curl -s -H "Accept-Encoding: gzip,deflate" http://myserver/api/genes > genes.gz
```

# Modules

## Centromere Core

### Customizing Model Classes

By default, all fields in classes that implement `Model` are exposed as valid query string parameters in the web services layer.  You can further customize a resource's query parameters by applying several annotations:

```java
public class Gene implements Model<String> {

	private String id;
	private Long entrezGeneId;
	@Alias("symbol") private String primaryGeneSymbol;
	private Integer taxId;
	@Ignored private String locusTag;
	private String chromosome;
	@Ignored private String chromosomeLocation;
	@Ignored private String description;
	private String geneType;
	@Alias("alias") private Set<String> aliases;
	@Aliases({
		@Alias(value = "isKinase", fieldName = "attributes.kinase"),
		@Alias(value = "isCgcGene", fieldName = "attributes.cgcGene")
	})
	private Map<String,String> attributes;

	/* Getters and Setters */
}
```

The `Alias` annotation allows query string parameters to map to entity fields of a different name, or to nested fields, with an optional value of `Evaluation`, different from the standard equality test.

It is also possible to customize HATEOAS link generation in the web service layer by using the `ForeignKey` annotation:

```java
public class CopyNumber implements Model<String> {

	private String id;

	@ForeignKey(model = Sample.class, relationship = ForeignKey.Relationship.MANY_TO_ONE, rel = "sample")
	private String sampleId;

	@ForeignKey(model = Gene.class, relationship = ForeignKey.Relationship.MANY_TO_ONE,
			rel = "gene", field = "entrezGeneId")
	private String geneId;

	@Aliases({
			@Alias(value = "signalGreaterThan", evaluation = Evaluation.GREATER_THAN),
			@Alias(value = "signalLessThan", evaluation = Evaluation.LESS_THAN),
			@Alias(value = "signalBetween", evaluation = Evaluation.BETWEEN),
			@Alias(value = "signalOutside", evaluation = Evaluation.OUTSIDE_INCLUSIVE)
	})
	private Double signal;

	/* Getters and Setters */

}
```

All model classes will have `self` links created when HATEOAS-supported media types are requested in the web services layer.  Classes with `ForeignKey`-annotated field will also get links generated based upon the relationship described in the annotation parameters:

```javascript
{
	"id": "123",
	"sampleId": "456",
	"geneId": 789,
	"signal": 2.45,
	"links": [
		{ "rel": "self", "href": "/api/cnv/123" },
		{ "rel": "sample", "href": "/api/samples/456" },
		{ "rel": "gene", "href": "/api/genes?entrezGeneId=789" }
	]
}
```

## Centromere MongoDB

### MongoDB Repositories

The `GenericMongoRepository` is the MongoDB implementation of `RepositoryOperations`, utilizing Spring Data MongoDB's `MongoTemplate` for query execution and object mapping.  Before creating repository classes, you should configure your database connection:

```java
/* Example configuration for a MongoDB 3.x instance */
@Configuration
@PropertySource({ "classpath:mongodb-data-source.properties" })
public class MongoConfig extends AbstractMongoConfiguration {

	@Autowired private Environment env;

	@Override
	public String getDatabaseName(){
		return env.getRequiredProperty("mongo.name");
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		ServerAddress serverAddress = new ServerAddress(env.getRequiredProperty("mongo.host"));
		List<MongoCredential> credentials = new ArrayList<>();
		credentials.add(MongoCredential.createScramSha1Credential(
				env.getRequiredProperty("mongo.username"),
				env.getRequiredProperty("mongo.name"),
				env.getRequiredProperty("mongo.password").toCharArray()
		));
		return new MongoClient(serverAddress, credentials);
	}

}
```

A simple implementation of a MongoDB repository class looks like this:

```java
@Repository
public class GeneRepository extends GenericMongoRepository<Gene, String> {
	@Autowired
	public GeneRepository(MongoTemplate mongoTemplate) {
		super(mongoTemplate, Gene.class);
	}
}
```

You can also expand on the basic operation set of the repository class by defining your own methods:

```java

@Repository
public class GeneRepository extends GenericMongoRepository<Gene, String> {

    @Autowired
	public GeneRepository(MongoTemplate mongoTemplate) {
		super(mongoTemplate, Gene.class);
	}

    /* Using the MongoDB driver `Query` class and API */

    public List<Gene> findByEntrezGeneId(Long entrezGeneId){
        return this.getMongoOperations()
            .find(new Query(Criteria.where("entrezGeneId").is(entrezGeneId));
    }

    /* Using `QueryCriteria` and the Centromere repository API */

    public List<Gene> findByGeneSymbolAlias(String alias){
        return this.find(new QueryCriteria("aliases", alias, Evaluation.EQUALS));
    }

}

```

## Centromere SQL

### SQL Repositories

The `GenericJdbcRepository` is the JDBC SQL database implementation of `RepositoryOperations`.  This repository implementation is based on  `com.nurkiewicz.jdbcrepository.JdbcRepository`, but uses a more complex version of the `TableDescription` class, and a custom SQL generation class, `SqlBuilder`.  Much like with `JdbcRepository`, you define a `GenericJdbcRepository` using a `ComplexTableDescription`, `RowMapper`, and optional `RowUnmapper`.  For example:

```java
/* Subject data stored in a single table */
@Repository
public SubjectRepository extends GenericJdbcRepository<Subject, Integer> {

	@Autowired
	public SubjectRepository(DataSource dataSource){
	    super(dataSource, new SubjectTableDescription(), new SubjectMapper(), new SubjectUnmapper());
	}

	public static class SubjectTableDescription extends ComplexTableDescription {
		public SubjectTableDescription(){
			super(
				"subjects", // table name
				Arrays.asList("subject_id") // primary key ID columns
			);
		}
	}

	public static class SubjectMapper implements RowMapper<Subject> {
		@Override
		public Subject mapRow(ResultSet rs, int i){
			Subject subject = new Subject();
			subject.setId(rs.getInt("subject_id"));
			subject.setName(rs.getString("name"));
			subject.setAge(rs.getInt("age"));
			subject.setGender(rs.getString("gender"));
			List<Attributes> attributes = new ArrayList();
			if (rs.getString("attributes") != null){
				for (String attribute: rs.getString("attributes").split(":::")){
					String[] bits = attributes.split("::");
					attributes.add(new Attribute(bits[0], bits[1]));
				}
			}
			subject.setAttributes(attributes);
			return subject;
		}
	}

	public static class SubjectUnmapper implements RowUnmapper<Subject> {
		@Override
		public Map<String,Object> mapColumns(Subject subject){
			Map<String,Object> map = new HashMap();
			map.put("subject_id", subject.getId());
			map.put("name", subject.getName());
			map.put("age", subject.getAge());
			map.put("gender", subject.getGender());
			boolean flag = false;
			StringBuilder sb = new StringBuilder();
			for (Attribute attribute: subject.getAttributes()){
				if (flag){
					sb.append(":::");
				}
				flag = true;
				sb.append(attribute.getName()).append("::").append(attribute.getValue());
			}
			map.put("attributes", sb.toString());
			return map;
		}
	}

}
```

If the `RowUnmapper` is left out, the repository will be read-only, and all `insert` or `update` method calls will result in an exception.

Storing `Subject` records with their `Attributes` in a single table is simple enough, but it is not ideal.  The better solution would be to create a separate table, `subject_attributes`, with a many-to-one relationship with the `subjects` table.  This would allow you to index the attribute names for easier queries:

```java
/* Subject data stored in two MySQL tables */
@Repository
public SubjectRepository extends GenericJdbcRepository<Subject, Integer> {

	@Autowired
	public SubjectRepository(DataSource dataSource){
	    super(dataSource, new SubjectTableDescription(), new SubjectMapper());
	}

	@Override
	public <S extends Subject> S insert(S entity) {

    		KeyHolder keyHolder = new GeneratedKeyHolder();
    		this.getJdbcTemplate().update(
    				new PreparedStatementCreator() {
    					@Override
    					@SuppressWarnings("JpaQueryApiInspection")
    					public PreparedStatement createPreparedStatement(Connection connection) throws
    							SQLException {
    						PreparedStatement ps = connection.prepareStatement(
    								"INSERT INTO `subjects` (name, gender, age) VALUES (?, ?, ?);",
    								new String[] {"id"}
    						);
    						ps.setString(1, entity.getName());
    						ps.setString(2, entity.getGender());
    						ps.setInt(3, entity.getAge());
    						return ps;
    					}
    				},
    				keyHolder
    		);
    		Integer subjectId = keyHolder.getKey().intValue();
    		entity.setId(Integer.toString(geneId));

    		if (entity.getAttributes() != null) {
    			for (Attribute attribute : entity.getAttributes()) {
    				this.getJdbcTemplate().update(
    						"INSERT INTO `subject_attributes` (subject_id, name, value) VALUES (?, ?, ?)",
    						subjectId, attribute.getName(), attribute.getValue());
    			}
    		}

    		return entity;

    	}

	public static class SubjectTableDescription extends ComplexTableDescription {
		public SubjectTableDescription(){
			super(
				"subjects", // table name
				Arrays.asList("s.subject_id"), // primary key IDs
				"s.*, GROUP_CONCAT(CONCAT(a.name, '::', a.value) SEPARATOR ':::') as attributes", // SELECT statement
				"subjects s left join subject_attributes a on s.subject_id = a.subject_id", // FROM statement
				"s.subject_id" // GROUP BY statement
			);
		}
	}

	public static class SubjectMapper implements RowMapper<Subject> {
		@Override
		public Subject mapRow(ResultSet rs, int i){
			Subject subject = new Subject();
			subject.setId(rs.getInt("subject_id"));
			subject.setName(rs.getString("name"));
			subject.setAge(rs.getInt("age"));
			subject.setGender(rs.getString("gender"));
			List<Attributes> attributes = new ArrayList();
			if (rs.getString("attributes") != null){
				for (String attribute: rs.getString("attributes").split(":::")){
					String[] bits = attributes.split("::");
					attributes.add(new Attribute(bits[0], bits[1]));
				}
			}
			subject.setAttributes(attributes);
			return subject;
		}
	}

}
```

Now we have a CRUD repository that pulls data from two tables into a single model class.  

## Centromere JPA

### JPA Repositories

The `GenericMongoRepository` is the MongoDB implementation of `RepositoryOperations`, utilizing Spring Data MongoDB's `MongoTemplate` for query execution and object mapping.  Before creating repository classes, you should configure your database connection:

```java
/* Example configuration for a MongoDB 3.x instance */
@Configuration
@PropertySource({ "classpath:mongodb-data-source.properties" })
public class MongoConfig extends AbstractMongoConfiguration {

	@Autowired private Environment env;

	@Override
	public String getDatabaseName(){
		return env.getRequiredProperty("mongo.name");
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		ServerAddress serverAddress = new ServerAddress(env.getRequiredProperty("mongo.host"));
		List<MongoCredential> credentials = new ArrayList<>();
		credentials.add(MongoCredential.createScramSha1Credential(
				env.getRequiredProperty("mongo.username"),
				env.getRequiredProperty("mongo.name"),
				env.getRequiredProperty("mongo.password").toCharArray()
		));
		return new MongoClient(serverAddress, credentials);
	}

}
```

A simple implementation of a MongoDB repository class looks like this:

```java
@Repository
public class GeneRepository extends GenericMongoRepository<Gene, String> {
	@Autowired
	public GeneRepository(MongoTemplate mongoTemplate) {
		super(mongoTemplate, Gene.class);
	}
}
```

You can also expand on the basic operation set of the repository class by defining your own methods:

```java

@Repository
public class GeneRepository extends GenericMongoRepository<Gene, String> {

    @Autowired
	public GeneRepository(MongoTemplate mongoTemplate) {
		super(mongoTemplate, Gene.class);
	}

    /* Using the MongoDB driver `Query` class and API */

    public List<Gene> findByEntrezGeneId(Long entrezGeneId){
        return this.getMongoOperations()
            .find(new Query(Criteria.where("entrezGeneId").is(entrezGeneId));
    }

    /* Using `QueryCriteria` and the Centromere repository API */

    public List<Gene> findByGeneSymbolAlias(String alias){
        return this.find(new QueryCriteria("aliases", alias, Evaluation.EQUALS));
    }

}


## Centromere Data Import CLI

### Import Configuration

The `centromere-data-import-cli` module makes use of the component classes defined in the `centromere-core` module.  To create a command line import tool for your data import components, you must first define a configuration class that extends `DataImportConfigurer`:

```java
@Configuration
@ComponentScan(basePackages = { "me.woemler.dataimport" })
public class ImportConfig extends DataImportConfigurer {

}
```

The `DataImportConfigurer` will initialize several beans that help manage the import process, including the `DataImportManager`, which handles mapping your implemented `RecordProcessor` classes to user-inputted data files on the command line.  By default, the `DataImportManager` will pick up on all `RecordProcessor` instances that are annotated with the `@DataTypes` annotation and create an association between the provided data type labels and their processor classes.  You can append-to or overwrite the default data type and data set mapping behavior by overriding the xxx methods:

```java
@Configuration
@ComponentScan(basePackages = { "me.woemler.dataimport" })
public class ImportConfig extends DataImportConfigurer {

    @Autowired private ApplicationContext context;

    @Override
    public Map<String, DataSetMetadata> configureDataSetMappings(Map<String, DataSetMetadata> dataSetMap){
        dataSetMap.put("test", new BasicDataSetMetadata(xxx));
        return dataSetMap;
    }

    @Override
    public Map<String, RecordProcessor> configureDataTypeMappings(Map<String, RecordProcessor> dataTypeMap){
        dataTypeMap.put("mutations", context.getBean(MutationProcessor.class));
        return dataTypeMap;
    }

}
```

The `DataImportConfigurer` also creates an instance of a `CommandLineRunner` bean, which will accept and parse command line arguments, and then execute the appropriate actions.  This module utilizes [JCommander](http://jcommander.org/) to define and parse command line arguments, and the command line arguments are defined within the `ImportCommandArguments` and `AddCommandArguments` classes.  To utilize the default command line behavior, make use of the `CommandLineRunner` instance in your main class:

```java
public class Main {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImportConfig.class);
        CommandLineRunner runner = context.getBean(CommandLineRunner.class);
        runner.run(args);
    }
}
```

### Running the Import

Data import tools tools built with `centromere-data-import-cli` can run as executable JAR files on command line.  When run with the default arguments and configuration, such as is described above, the command line tool syntax is as follows:

```
# Import command
Usage: import [options]
  Options:
    -d, --data-set
       Data set label or JSON representation.  If not provided, no data set will
       be associated with the file.
  * -t, --data-type
       Data type label for the target file.  Required.
  * -i, --input
       File to be imported.  Required.
    --skip-invalid-data-sets
       When true, records and files associated with invalid or existing data
       sets will be skipped, rather than throw an exception.
       Default: false
    --skip-invalid-genes
       When true, records that do not match a valid gene will be skipped, rather
       than throw an exception.
       Default: false
    --skip-invalid-records
       When true, records that fail validation will be skipped, rather than
       throwing an exception.
       Default: false
    --skip-invalid-samples
       When true, records that do not match valid samples will be skipped,
       rather than throw an exception.
       Default: false
    -T, --temp-dir
       Directory to write temporary files to.  Defaults to '/tmp'.
       Default: /tmp

# Add command
Usage: add category label body
  Positional arguments:
    category
      Used to specify the record type to be added.  Currently supports 'data_type'
      and 'data_set'.
    label
      Unique string identifier to associate with the record to be added.  Used
      for identification in the import command.
    body
      Content of the record.  For the 'data_type' category, this should be the
      name of a RecordProcessor class or existing bean.  For the 'data_set'
      category, this should be a JSON representation of the data set metadata.
```


## Centromere Web

### Web Service Configuration

The easiest way to configure Centromere is to use the available auto-configuration annotations in a Spring Boot application class or supporting configuration class: `@AutoConfigureWebServices`, `@AutoConfigureWebSecurity`, and `@AutoConfigureApiDocumentation`.  This will handle all of the required web context configuration and bean registration for the web services, API documentation, and security features:

```java
@Configuration
@AutoConfigureWebServices
@AutoConfigureWebSecurity
@AutoConfigureApiDocumentation
@ComponentScan(basePackages = { "me.woemler.myapp" })
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(Application.class);
	}

	public static void main(String[] args){
		SpringApplication.run(Application.class, args);
	}

}
```
