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

package com.blueprint.centromere.core.dataimport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanWrapper;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * Utility methods used for data import classes that depend upon {@link com.blueprint.centromere.core.model.Model}
 *   reflection.
 *
 * @author woemler
 * @since 0.5.0
 */
public class ModelReflectionUtils {

  /**
   * Tests whether a given field is persistable, but is not a related {@link org.springframework.data.mongodb.core.mapping.Document}
   *   object.  Allows supplying a list of extra fields to be ignored.
   *
   * @param field reflected field.
   * @param ignoredFields list of field names to ignore.
   * @return true is field is persistable, but not an entity.
   */
  public static boolean isPersistableNonEntityField(Field field, List<String> ignoredFields){
    return field.isSynthetic()
        || ignoredFields.contains(field.getName())
        || field.isAnnotationPresent(DBRef.class);
  }

  /**
   * Tests whether a given field is persistable, but is not a related {@link org.springframework.data.mongodb.core.mapping.Document}
   *   object.
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

}
