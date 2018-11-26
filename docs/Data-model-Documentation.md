# Creating Data Model and Repository Classes

## Models

The core of every Centromere data warehouse are the data model classes, which are represented as implementations of the `Model` interface.  The entities serve as data transfer objects (DTOs) to move data between application layers, and define how data is queried and represented in the web services.  While models can themselves represent aggregate data or database views, they should be considered atomic entities when reading and serving data through your repositories and REST API, as each model will have its own data access object (DAO) repository class and REST endpoints.

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

  @Override
  public String getId(){ return id; }

  /* Getters and Setters */
  
}
```

The `Model` interface defines three key features of all model classes:

- They must have an identifying attribute, which is intended to serve as the primary key ID (in this case, a `String` representation of a MongoDB `ObjectID`).
- This identifier must be a serializable object, for the sake of representation in a URL.
- The attributes in the object may be filtered in web service responses, using object transformers like Jackson.

Model object persistence will be handled depending on the database back-end and integration module being used (eg. `centromere-mongodb`).  For some implementations, the above `Model` example is all you need to get started, but sometimes additional customization is required to get the maximum utility.

### MongoDB Models

When using MongoDB and the `centromere-mongodb` module, it makes sense to expand on the example model above to take advantage of features available in MongoDB and Spring Data MongoDB.  For example, we can define the name of the collection our data will be persisted to with the `@Document` annotation, and add indexes with the `@Indexed` annotation:

```java
/* MongoDB model */
@Document(collection = "genes")
public class Gene implements Model<String> {

  @Id private String id;
  @Indexed private Long entrezGeneId;
  @Indexed private String geneSymbol;
  private String species;
  private String chromosome;
  private Set<String> aliases;
  private Map<String,String> attributes;
  
  @Override
  public String getId(){ return id; }
  
  /* Getters and Setters */
  
}
```

By default, Spring Data MongoDB will map model attributes with the name `id` or annotated with `@Id` to the document primary key `_id` attribute.  Using a `String` type for your ID field will allow for seamless translation between the MongoDB ObjectID type on the backend and simple `String` character representation in Java. Collections, such as `List` and `Map` implementations will also be seamlessly translated to BSON arrays and maps in the database, so there is no need to create separate collections and relationships to persist the `aliases` and `attributes` fields in the example model above.

### Model Relationships

xxx

## Repositories

All Centromere repository classes implement the base `ModelRepository` interface, which defines all of the basic CRUD operations that all Centromere repositories should dataSetSupport.  This interface is based on Spring Data's `PagingAndSortingRepository`, but with some additional method definitions for dynamic query operations.  The database-specific implementations also include several methods specific to those data stores.  A MongoDB repository implementation for the above `Gene` model class might look like this:

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
QueryCriteria criteria = new QueryCriteria("publicId", 1L, Evaluation.EQUALS);

/*
Will be translated based upon the database implementation to:
   WHERE publicId = 1 # for SQL
   or
   {"publicId": 1} # for MongoDB
*/

```
