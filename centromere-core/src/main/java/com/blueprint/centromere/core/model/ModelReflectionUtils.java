/*
 * Copyright 2018 the original author or authors
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanWrapper;

/**
 * Utility methods used for data import classes that depend upon {@link com.blueprint.centromere.core.model.Model}
 *   reflection.
 *
 * @author woemler
 * @since 0.5.0
 */
public class ModelReflectionUtils {

  /**
   * Tests whether a given field is persistable.  Allows supplying a list of extra fields to be ignored.
   *
   * @param field reflected field.
   * @param ignoredFields list of field names to ignore.
   * @return true is field is persistable, but not an entity.
   */
  public static boolean isPersistableNonEntityField(Field field, List<String> ignoredFields){
    return field.isSynthetic()
        || ignoredFields.contains(field.getName());
  }

  /**
   * Tests whether a given field is persistable, but is not a related object.
   *
   * @param field reflected field.
   * @return true is field is persistable, but not an entity.
   */
  public static boolean isPersistableNonEntityField(Field field){
    return isPersistableNonEntityField(field, new ArrayList<>());
  }

  /**
   * Returns a list of field names used to access field values in a model wrapped with {@link BeanWrapper}.
   *   Allows specified fields to be skipped.
   *
   * @param model model class
   * @param ignoredFields field to be ignored
   * @return list of fields
   */
  public static List<String> getPersistableNonEntityFieldNames(Class<?> model, List<String> ignoredFields){
    List<String> columns = new ArrayList<>();
    Class<?> currentClass = model;
    while (currentClass.getSuperclass() != null){
      for (Field field : currentClass.getDeclaredFields()) {
        if (ModelReflectionUtils.isPersistableNonEntityField(field, ignoredFields)) continue; // skip these fields
        columns.add(field.getName());
      }
      currentClass = currentClass.getSuperclass();
    }
    return columns;
  }

  /**
   * Returns a list of field names used to access field values in a model wrapped with {@link BeanWrapper}.
   *
   * @param model model class
   * @return list of fields
   */
  public static List<String> getPersistableNonEntityFieldNames(Class<?> model){
    return getPersistableNonEntityFieldNames(model, new ArrayList<>());
  }

  /**
   * Returns a collection of {@link Field} objects for all attributes of the requested type that 
   *   are annotated with {@link Linked}, representing relationships between multiple {@link Model}.
   * 
   * @param model model to inspect
   * @return list of fields with annotations
   */
  public static List<Field> getLinkedModelFields(Class<?> model){
    List<Field> fields = new ArrayList<>();
    Class<?> currentClass = model;
    while (currentClass.getSuperclass() != null){
      for (Field field: currentClass.getDeclaredFields()){
        if (field.isAnnotationPresent(Linked.class)) fields.add(field);
      }
      currentClass = currentClass.getSuperclass();
    }
    return fields;
  }

  /**
   * Returns a list of {@link Field} objects with {@link Linked} annotations found present with 
   *   matching relationship names.
   * 
   * @param model model to inspect
   * @param rel relationship name
   * @return list of annotation instances
   */
  public static List<Field> getLinkedAnnotationsFromRelName(Class<?> model, String rel){
    List<Field> list = new ArrayList<>();
    for (Field field: getLinkedModelFields(model)){
      if (field.isAnnotationPresent(Linked.class)){
        Linked linked = field.getAnnotation(Linked.class);
        if (rel.toLowerCase().equals(linked.rel().toLowerCase())){
          list.add(field);  
        }
      }
    }
    return list;
  }
  
  

}
