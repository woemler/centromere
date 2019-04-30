#!/usr/bin/env bash
mvn -f centromere-parent/pom.xml clean javadoc:aggregate install jacoco:report-aggregate
