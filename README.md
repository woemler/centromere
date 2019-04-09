# Centromere  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.oncoblocks.centromere/centromere-core/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.oncoblocks.centromere%22) [![Build Status](https://travis-ci.org/blueprintmedicines/centromere.svg?branch=master)](https://travis-ci.org/blueprintmedicines/centromere)  [![Codacy Badge](https://api.codacy.com/project/badge/Grade/5ab173c39407432695f6a5b268135a27)](https://www.codacy.com/app/willoemler/centromere?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=blueprintmedicines/centromere&amp;utm_campaign=Badge_Grade)  [![Gitter](https://badges.gitter.im/blueprintmedicines/centromere.svg)](https://gitter.im/blueprintmedicines/centromere?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

### Genomic data warehousing, file parsing, and RESTful web services made easy.

## About
Centromere is a set of tools for developing scalable data warehouses and RESTful web services.  It is designed to tackle the problems inherent in developing data warehouses for genomic data, where data structures can vary greatly and scale fast, and where use-cases must be flexible to accommodate shifting business needs.  Centromere is developed in Java using the open-source, enterprise-grade Spring Framework, and supports integration with multiple database technologies.  You can use Centromere to create a new data warehouse from scratch, or bootstrap one or more existing databases, and make your data available via a customizable REST API.

Here are a few ways Centromere can help make your data warehouse and REST API better:
- Support classes for quickly creating data models and data access objects (DAOs).
- Components for reading standard column- and row-based data files, and rapidly developing extract-transform-load (ETL) pipelines.
- Spring Boot initializer for exposing your data repository as a REST web service with:
  - A flexible and easy-to-use query API
  - Simple token-based security
  - Automatic API documentation with [Swagger](https://swagger.io/)
  - Support for exporting data in JSON, XML and tab-delimited-table formats.

Centromere does _**not**_ contain a pre-defined data model, but rather provides you the tools to quickly generate your own model and expose your data through scalable repositories and web services.

## Requirements

These are the current requirements for developing data warehouses with Centromere:

- Java JDK 8+
- Maven 3
- MongoDB 3+

\* Support for additional database technologies is coming soon. 

## Building

You can build the entire project, run tests, generate JavaDocs and JaCoCo reports using the `centromere-parent` POM file:

```bash
mvn clean javadoc:javadoc install jacoco:report-aggregate
```

## Maven Artifacts

Artifacts for Centromere release builds are available from the Maven Central Repository:

```xml
COMING SOON
```

## Contact

For questions about Centromere, or if you are interested in contributing, 
please contact:
  - [Will Oemler](mailto:woemler@blueprintmedicines.com), Blueprint Medicines

## License

Copyright 2018 William Oemler, Blueprint Medicines

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
