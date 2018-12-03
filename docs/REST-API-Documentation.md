# REST API Documentation

## Making Requests

### Filtering Results

Most `GET` method query operations support a flexible query API, enabling search results to be filtered by model attributes. This filtering is performed by adding query string parameters for the desired attribute, with appended operator suffixes, if desired.  For example, `/api/search/users?name=Steve` would return all `user` records that have a `name` attribute equal to `Steve`.  Alternatively, you could modify that query to return return `user` records where the `name` attribute only starts-with `Steve` like so: `/api/search/users?nameStartsWith=Steve`. The list of supported `QueryCriteria` operators is listed below.

Operator | Description | Example
-------- | ----------- | -------
`Equals` | Attribute exactly equals input.  Equivalent to using only the attribute name with no operator suffix.  | `?name=Steve` or `?nameEquals=Steve` are equivalent.
`NotEquals` | Attribute does not equal input. | `?statusNotEquals=active`
`In` | Attribute exactly equals at least one of the multiple inputs. Equivalent to using no operator suffix and comma-separated inputs. | `?species=mouse,human` or `?speciesIn=mouse,human` are equivalent.
`NotIn` | Attribute does not equal any of the multiple inputs. | `?speciesNotIn=mouse,human`
`Like` | Fuzzy attribute match to input.  Works like a case-insensitive substring match for character fields. | `?geneLike=akt`
`NotLike` | Inverse fuzzy attribute match to inout. Will exclude all records that pass a case-insensitive substring match on a character field. | `?geneNotLike=akt`
`StartsWith` | String attribute begins with the input characters. | `?nameStartsWith=Joe`
`EndsWith` | String attribute ends with input characters. | `?nameEndsWith=Smith`
`GreaterThan` | Numerical attribute is greater than the input value. | `?signalGreaterThan=2.5`
`LessThan` | Numerical attribute is less than the input value. | `?signalLessThan=2.5`
`GreaterThanOrEquals` | Numerical attribute is greater than or equal to the input value. | `?signalGreaterThanOrEquals=2.5`
`LessThanOrEquals` | Numerical attribute is less than or equal to the input value. | `?signalLessThanOrEquals=2.5`
`Between` | Numerical attribute value is between the two input values. | `?valueBetween=1.0,2.5`
`BetweenIncluding` | Numerical attribute value is between or equal to the two input values. | `?valueBetweenIncluding=1.0,2.5`
`Outside` | Numerical attribute is greater than or less than the two input values. | `?valueOutside=1.0,2.5`
`OutsideIncluding` | Numerical attribute is greater than, less than, or equal to the two input values. | `?valueOutsideIncluding=1.0,2.5`
`IsNull` | Attribute value is null. | `?statusIsNull`
`IsNotNull`  | Attribute value is not null. | `?statusIsNotNull`
`IsTrue` | Attribute is a boolean true value. | `?flagIsTrue`
`IsFalse` | Attribute is a boolean false value. | `?flagIsFalse`

### Pagination

All endpoints that return collections of records support pagination, allowing the retrieval of partial responses.  Page requests are triggered by performing requests using the query string parameters `limit` and `offset` or `page`.  The `size` parameter specifies the maximum number of records to be returned in the page. The `offset` parameter specifies the number of records to be skipped before the first record is returned. The `page` parameter determines which record page will be returned, and can be used in conjunction with `size`. The default page size is 1000 records and page numbering starts at 0. 

```
// To get the first 100 records in a collection
GET /api/search/mutations?size=100

// To get records 901-1000
GET /api/search/mutations?size=100&page=10

// To get 200 records, skipping the first 100
GET /api/search/mutations?offset=100&size=200
```

Paginated records are returned enveloped and annotated with the number of pages, total records, and include links to next/previous pages.

```
GET /api/search/users?size=5&page=1

{
  "page": {
    "totalElements": 20,
    "number": 1,
    "size": 5,
    "totalPages": 2
  },
  links: [
    {
      "rel": "first",
      "href": "/api/search/users?page=0&size=5"
    }, {
     "rel": "prev",
     "href": "/api/search/users?page=0&size=5"
    }, {
      "rel": "next",
      "href": "/api/search/users?page=2&size=5"
    }, {
      "rel": "last",
      "href": "/api/search/users?page=3&size=5"
    }
  ],
  "content": [
    { 
       "id": "b98394743f20y92bn",
       "username": "jsmith",
       "email": "jsmith@email.com"
     },
    ...
  ]
}
```

### Sorting

API endpoints that return collections all support record sorting, using the `sort` query string parameter.  By default, records are placed in ascending order, but you can specify descending order as well.

```
// Fetches all samples and sorts alphabetically by name
GET /api/search/samples?sort=name 

// Fetches all gene records and sorts by Entrez Gene ID, numerically ascending
GET /api/search/genes?sort=entrezGeneId

// Fetches all samples and sorts alphabetically by name, in descending order (Z to A)
GET /api/search/samples?sort=name,desc
```

### Supported Media Types

Centromere by default supports three output formats: JSON, XML, and tab-delimited text. You can specify the desired content type by using either the `Accept` header or the `format` query string parameter in your request.

```
# JSON
curl -H "Accept: application/json" http://mycentromere/api/search/data
curl http://mycentromere/api/search/data?format=json

# XML
curl -H "Accept: application/xml" http://mycentromere/api/search/data
curl http://mycentromere/api/search/data?format=xml

# Text
curl -H "Accept: text/plain" http://mycentromere/api/search/data
curl http://mycentromere/api/search/data?format=text
```  
  
Both JSON and XML also support the inclusion of HAL-formatted hypermedia, which is described in the next section in more detail.

```
# HAL+JSON
curl -H "Accept: application/hal+json" http://mycentromere/api/search/data
curl http://mycentromere/api/search/data?format=haljson

# HAL+XML
curl -H "Accept: application/hal+xml" http://mycentromere/api/search/data
curl http://mycentromere/api/search/data?format=halxml
```  

### Hypermedia

Centromere aims to enable the creation of level 3 REST APIs, and as such, supports hypermedia as the engine of application state (HATEOAS).  When using the media types `application/hal+json` or `application/hal+xml` (or the query string parameters `format=haljson` or `format=halxml`), the response will be wrapped and annotated with HAL-formatted links:

```
curl -H "Accept: application/hal+json" http//mycentromere/api/search/datafiles

{
  links: [
    {
      rel: "self",
      href: "http//mycentromere/api/search/datafiles"
    }
  ],
  content: [
    {
      id: 123,
      filePath: "/path/to/my/file",
      samples: [456, 789, 1011],
      studyId: 3,
      links: [
        {
          rel: "self",
          href: "http//mycentromere/api/search/datafiles/123" 
        }, {
          rel: "samples",
          href: "http//mycentromere/api/search/samples?id=456,789,1011" 
        }, {
          rel: "study",
          href: "http//api.oncoblocks.org/studies?id=3"
        }
      ]
    }, {...}
  ]
  
}
```

### Excluding and Including Attributes

You can modify what model attributes are returned in the response using the `exclude` and `include` query parameters.  For example, given a model definition:

```java
public class Person implements Model<String> {
  
  @Id private String id;
  private String name;
  private String email;
  private Date dateJoined;
  private Map<String, String> details;
  
  /* getters and setters */
  
}
```

You can remove attributes from the response objects by using the `exclude` query string parameter:

```
GET /api/search/people?exclude=id,dateJoined,details

response = [
  {
    name: "Joe Smith",
    email: "joesmith@email.com"
  }, {
    name: "Kim Smith",
    email: "kim.smith@email.com"
  }, ...
]
```

Sometimes it is easier to simply specify the attributes you do want returned, rather than excludint those you do not.  The same result displayed above could be accomplished using the `include` query string parameter:

```
GET /api/search/people?include=name,email

response = [
  {
    name: "Joe Smith",
    email: "joesmith@email.com"
  }, {
    name: "Kim Smith",
    email: "kim.smith@email.com"
  }, ...
]
```

## API Security 

By default, Centromere web services are unsecured and all operations are open to all users.  You can easily secure your web service using one of the built-in security configurations, or by adding your own [Spring Security-based](https://spring.io/projects/spring-security) configurations.  When using the `@AutoConfigureCentromere` annotation on a Spring Boot application class, you can specify the pre-configure security options you would like to use. For example:

```java
@AutoConfigureCentromere(webSecurity = WebSecurityConfig.SIMPLE_TOKEN_SECURITY_PROFILE)
public class Application extends CentromereWebInitializer {
                                 
  public static void main(String[] args) {
    CentromereWebInitializer.run(WebTestInitializer.class, args);
  }

}

```

### Simple Token Security

The `SIMPLE_TOKEN_SECURITY` option enables a simple, stateless, token-based method for securing web service applications.  When enabled, all requests to the API will require the user to supply an authentication token in order to perform the requested operation. This security option requires that an instance of a [UserDetailsService](https://docs.spring.io/spring-security/site/docs/4.2.6.RELEASE/apidocs/org/springframework/security/core/userdetails/UserDetailsService.html) bean be present in the application to manage user accounts, though how user records are created and persisted is left up to the developer. Once configured and running, users can receive a security token by passing their credentials to the `/authenticate` endpoint:

```
curl -u user:password -X POST http://mycentromere/authenticate

response = {
  token: 'user:1542220218016:beed30c0e27b2059ac36e798ce09c224',
  username: 'user',
  issued: 2018-11-13 18:30:18.16 UTC,
  expires: 2018-11-14 18:30:18.16 UTC
}
```

The retrieved `token` value should be passed when making requests to any API endpoint using the `X-Auth-Token` header:

```
curl -H 'X-Auth-Token: user:1542220218016:beed30c0e27b2059ac36e798ce09c224' http://mycentromere/api/data
```

Requests made to protected API endpoints without giving a valid security token will result is a `401 Unauthorized` error.

## Monitoring

Centromere uses [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) to expose several additional REST endpoints that provide information about the health and configuration of the web services. The Actuator root can be found at the URL: `/actuator`. Database and application health information cane be found at `/actuator/health`.  General application information can be found at `/actuator/info`.

## Endpoint Reference

### API Root Endpoints

URI | Description
--- | -----------
`/api` | The root URL for the web services.  All model resource endpoints should fall under this URL.
`/api/search` | Model search root.  Supports standard CRUD operations for querying and manipulating model resources.
`/api/aggregation` | Model resource aggregation root. These endpoints support various operations for summarizing or transforming model records.

### Search

Method | URI | Description
------ | --- | -----------
`GET` | `/api/search/{model}` | Performs a search of the `model` resource repository.  Supports `QueryCriteria` filtering, pagination, and field exclusion.
`POST` | `/api/search/{model}` | Attempts to create a new instance of the target `model`.
`GET` | `/api/search/{model}/{id}` | Fetches the instance of the `model` resource identified by its `id`.  Supports field exclusion.
`PUT` | `/api/search/{model}/{id}` | Attempts to update the instance of the target `model` identified by the supplied `id`.
`DELETE` | `/api/search/{model}/{id}` | Attempts to delete the instance of the target `model` identified by the supplied `id`.

### Aggregation

Method | URI | Description
------ | --- | -----------
`GET` | `/api/aggregation/{model}/count` | Returns a count of the number of `model` records that satisfy the query.  Supports `QueryCriteria` filtering.
`GET` | `/api/aggregation/{model}/distinct/{field}` | Returns a list of unique values of the requested `model` attribute, `field`.  Supports `QueryCriteria` filtering.
`GET` | `/api/aggregation/{model}/group/{field}` | Returns a key-value collection of unique `field` values for the requested `model` and all records that have that value forthe requested attribute.  Supports `QueryCriteria` filtering.

### Actuator

Method | URI | Description
`GET` | `/actuator` | The root URL for Actuator monitoring services.
`GET` | `/actuator/health` | Health information for the web application, database, and file system.
`GET` | `/actuator/info` | Basic information about the web application and dependencies.

### Authentication
Method | URI | Description
`POST` | `/authenticate` | Authentication entry point. Generates a token that can be used for subsequent requests.

## HTTP Status Codes 

The following HTTP status codes are used for responding to requests:

Code | Description
---- | -----------
`200 OK` | The request executed successfully.
`201 CREATED` | Indicates successful record creation from `POST` and `PUT` requests.
`400 BAD REQUEST` | Returned when a request is formatted incorrectly or supplies invalid parameters.
`401 UNAUTHORIZED` | User is not properly authenticated or unauthorized to access the requetsed resource. 
`404 NOT FOUND` | The requested resource does not exist.
`405 METHOD NOT ALLOWED` |  The HTTP request method is not supported for the URL.
`415 UNSUPPORTED MEDIA TYPE` | An unacceptable media type was requested.
`500 INTERNAL SERVER ERROR` | A server-side error prevented the request from completing.
