/*
 * Copyright 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueprint.centromere.core.repository;

import com.querydsl.core.types.Path;

/**
 * POJO that describes a model query parameter, used when reflecting {@link com.blueprint.centromere.core.model.Model}
 *   classes and mapping HTTP requests to {@link QueryCriteria}.
 *
 * @author woemler
 * @since 0.4.2
 */
public class QueryParameterDescriptor {

  private String name;
  private Path path;
  private Class<?> type;
  private Evaluation evaluation;

  public QueryParameterDescriptor() { }

  public QueryParameterDescriptor(String name, Path path, Class<?> type,
      Evaluation evaluation) {
    this.name = name;
    this.path = path;
    this.type = type;
    this.evaluation = evaluation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }

  public Evaluation getEvaluation() {
    return evaluation;
  }

  public void setEvaluation(Evaluation evaluation) {
    this.evaluation = evaluation;
  }

  @Override
  public String toString() {
    return "QueryParameterDescriptor{" +
        "name='" + name + '\'' +
        ", path=" + path +
        ", type=" + type +
        ", evaluation=" + evaluation +
        '}';
  }
}
