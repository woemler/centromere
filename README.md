# Centromere  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.oncoblocks.centromere/centromere-core/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.oncoblocks.centromere%22) [![Build Status](https://travis-ci.org/blueprintmedicines/centromere.svg?branch=master)](https://travis-ci.org/blueprintmedicines/centromere)  [![Codacy Badge](https://api.codacy.com/project/badge/Grade/5ab173c39407432695f6a5b268135a27)](https://www.codacy.com/app/willoemler/centromere?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=blueprintmedicines/centromere&amp;utm_campaign=Badge_Grade)  [![Gitter](https://badges.gitter.im/blueprintmedicines/centromere.svg)](https://gitter.im/blueprintmedicines/centromere?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

### Genomic data warehousing, file parsing, and RESTful web services made easy.

## About

Centromere aims to make the task of storing and working with processed 
genomic data easier, by provided a comprehensive tool-set for file parsing, 
database integration, and data access via REST web services.  Centromere 
includes a built-in data model for managing sample and experimental metadata,
gene expression, copy-number variation, mutation data, and more.  You can even 
extend or replace the core data model to suit your needs.  Centromere 
includes all of the required dependencies for running a full-featured data 
warehouse, and provides several starter projects to help you get up and 
running even faster.  Key features include:
 
- File processors for common bioinformatics data types and data sets (such as The Cancer Genome Atlas and Cancer Cell Line Encyclopedia).
- High-performance queries and flexible data-modeling, using MongoDB databases.
- A Command line utility for reading and writing data to the warehouse.
- REST web services, with a number of useful features:
    - Dynamic, user-defined queries.
    - Automatic [Swagger](http://swagger.io/) API documentation with.
    - Basic security and user-authentication.
    - Response object field filtering and formatting.
    
Centromere is built using the [Spring IO Platform](https://spring.io/platform), 
and leverages tools such as Spring Data, Spring Data REST, and Spring Security 
to deliver enterprise-grade data management solutions.    

## Important Note

The current development build of Centromere (0.5.0-SNAPSHOT) significantly 
overhauls the codebase, eliminating excess dependencies and streamlining the 
process to deployment.  If you are new to Centromere, I highly recommend 
waiting for the 0.5.0 release before getting started.  This is expected to 
be complete Q1 2017.  For a brief description of the current development 
road map, please see the `roadmap.md` document.

## Quick Start

#### Requirements

Centromere has the following requirements:

- JDK 1.8+
- Maven 2+
- MongoDB 3.0+ 

#### Create a new project with Maven and Spring Boot

*Instructions for using the starter project will go here*

## Documentation
*Link to updated docs will go here.*  

## Contact

For questions about Centromere, or if you are interested in contributing, 
please contact:
  - [Will Oemler](mailto:woemler@blueprintmedicines.com), Blueprint Medicines

## License

Copyright 2017 William Oemler, Blueprint Medicines

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
