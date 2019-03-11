/*
 * Copyright 2019 the original author or authors
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

package com.blueprint.centromere.core.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the annotated field in a {@link Model} class as representing a foreign-key relationship 
 *   within the source database. The {@link #model()} value indicates the linked model class, the
 *   {@link #field()} value indicates the field in the linked model class that is represented by
 *   this annotated field, and {@link #rel()} provides a name for this relationship.
 * 
 * @author woemler
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Linked {

  /**
   * The {@link Model} class type that the annotated field references.
   *
   * @return linked model class
   */
  Class<?> model();

  /**
   * Field name in the source {@link Model} type that the annotated field references.  Used to
   *   construct the query string options in the assembled link.
   *
   * @return the linked field in the origin class
   */
  String field() default "id";

  /**
   * Relationship name to be used in documentation and link creation.
   * 
   * @return relationship name
   */
  String rel() default "";
  
}
