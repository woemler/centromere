# Centromere  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.oncoblocks.centromere/centromere-core/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.oncoblocks.centromere%22) [![Build Status](https://travis-ci.org/blueprintmedicines/centromere.svg?branch=master)](https://travis-ci.org/blueprintmedicines/centromere)  [![Codacy Badge](https://api.codacy.com/project/badge/Grade/5ab173c39407432695f6a5b268135a27)](https://www.codacy.com/app/willoemler/centromere?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=blueprintmedicines/centromere&amp;utm_campaign=Badge_Grade)  [![codecov](https://codecov.io/gh/blueprintmedicines/centromere/branch/master/graph/badge.svg)](https://codecov.io/gh/blueprintmedicines/centromere)  [![Gitter](https://badges.gitter.im/blueprintmedicines/centromere.svg)](https://gitter.im/blueprintmedicines/centromere?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

### Genomic data warehousing, file parsing, and RESTful web services made easy.

## About

Centromere aims to make the task of storing and working with processed genomic data easier, by provided a comprehensive tool-set for file parsing, database integration, and data access via REST web services.  You can create your own data model, or use the built-in one, for managing sample or experimental metadata, genomic profile data, and anything else that you can think of.  Centromere includes all of the required dependencies for running a full-featured data warehouse, and provides several starter projects to help you get up and running even faster.  Key features include:
 
- File readers and processors for common bioinformatics data types.
- Preexisting data models for common data sets (such as The Cancer Genome Atlas).
- Integration with SQL and NoSQL databases.
- A Command line utility for reading and writing data to the warehouse.
- REST web services, with a number of useful features:
    - Dynamic, user-defined queries.
    - Automatic API documentation.
    - Basic security and user-authentication.
    - Response object field dfiltering and formatting.

## Quick Start

#### Requirements

Centromere has the following requirements:

- JDK 1.8+
- Maven 2+
- MongoDB 3.0+ or MySQL 5.5+

#### Create a new project with Maven and Spring Boot

A [starter project can be found here](#), which includes all of the required dependencies and a template for creating a new Centromere application.  Artifacts for Centromere release builds are available from the Maven Central Repository:

```xml
<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-core</artifactId>
    <version>0.4.2</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-data-import-cli</artifactId>
    <version>0.4.2</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-mongodb</artifactId>
    <version>0.4.2</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-jpa</artifactId>
    <version>0.4.2</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-sql</artifactId>
    <version>0.4.2</version>
</dependency>

<dependency>
    <groupId>org.oncoblocks.centromere</groupId>
    <artifactId>centromere-web</artifactId>
    <version>0.4.2</version>
</dependency>
```

##### 


## Documentation
Detailed documentation for Centromere and its modules can be **[found in the wiki](https://github.com/blueprintmedicines/centromere/wiki)**.  

## Modules

#### Centromere Core

The core module, containing common dependencies classes for creating data model, repository, and data import component classes.

#### Centromere Commons

Support for common bioinformatics data types and utilities.  Includes data model classes, custom repository implementations, and data import components.

#### Centromere Data Import CLI

Utility classes for creating command line import tools.  Builds on the data import components in the `centromere-core` module and adds some default behaviors for a command line interface.

#### Centromere MongoDB, JPA, and SQL

Database-specific implementations of the core repository interfaces and data import classes.  MongoDB is the preferred database technology, but MySQL is currently supported at an experimental level.

#### Centromere Web

The web module contains all components of the REST web services, including security and API documentation tools.

## Quick Start

Centromere requires Java JDK 8 or newer and Maven 2 or newer.

### Development Builds

For working with development builds, clone the repository and build using Maven:
 
 ```
 > cd centromere-parent
 > mvn -U clean install -Dgpg.skip
 ```


## Demo

A demo implementation of a Centromere data warehouse and web API is available as both a [live demo](https://centromere.herokuapp.com/index.html) and a [GitHub repository](https://github.com/blueprintmedicines/centromere-demo).  This demo utilizes a small data set of cancer genomic data from the TCGA to showcase the features of Centromere's web service and data import utilities.

## Contact

For questions about Centromere, or if you are interested in contributing, please contact:
  - [Will Oemler](mailto:woemler@blueprintmedicines.com), Blueprint Medicines

## License

Copyright 2016 William Oemler, Blueprint Medicines

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
