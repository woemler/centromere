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

package com.blueprint.centromere.core.commons.model;

import com.blueprint.centromere.core.commons.support.ManagedTerm;
import com.blueprint.centromere.core.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model for representing controlled and/or indexed terms.  Can be used for reference ontologies, 
 *   or cataloging metadata values for easy searching.
 * 
 * @author woemler
 */
@CompoundIndexes({
    @CompoundIndex(def = "{ 'model': 1, 'field': 1, 'term': 1 }", unique = true)
})
@Document
public class Term extends AbstractMongoModel {
	
	@Indexed private String term;
	@Indexed private String model;
	@Indexed private String field;
	private List<String> referenceIds;

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }
  
  @JsonIgnore
  public Class<?> getModelType() throws ClassNotFoundException {
    return Class.forName(model);
  }

  public String getModel() {
    return model;
  }

  public void setModel(Class<?> modelType){
    this.model = modelType.getName();
  }


  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public List<String> getReferenceIds() {
    return referenceIds;
  }

  public void setReferenceIds(List<String> referenceIds) {
    this.referenceIds = referenceIds;
  }
  
  public void addReferenceId(String referenceId){
    if (!referenceIds.contains(referenceId)) {
      referenceIds.add(referenceId);
    }
  }
  
  public void addReferenceIds(Collection<String> referenceIds){
    for (String ref: referenceIds){
      this.addReferenceId(ref);
    }
  }
  
  public static <T extends Model<?>> List<Term> getModelTerms(T model) throws IllegalAccessException {
    
    List<Term> terms = new ArrayList<>();
    
    if (!modelHasManagedTerms(model.getClass())) return terms;
    
    Class<?> current = model.getClass();
    
    while (current.getSuperclass() != null){
      
      for (Field field: current.getDeclaredFields()){
        
        if (field.isAnnotationPresent(ManagedTerm.class)){
          
          ManagedTerm annotation = field.getAnnotation(ManagedTerm.class);
          
          // String fields
          if (field.getType().isAssignableFrom(String.class)) {
            field.setAccessible(true);
            Term term = new Term();
            String val = (String) field.get(model);
            if (val != null && !val.trim().equals("")) {
              term.setTerm(val);
              term.setModel(model.getClass());
              term.setField(field.getName());
              term.setReferenceIds(Collections.singletonList(model.getId().toString()));
              terms.add(term);
            }
          } 
          // Map fields
          else if (Map.class.isAssignableFrom(field.getType())
              && field.getGenericType() instanceof ParameterizedType){
            Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            if (String.class.isAssignableFrom((Class<?>) args[0]) 
                && Serializable.class.isAssignableFrom((Class<?>) args[1])){
              field.setAccessible(true);
              Map<String, Serializable> map = (Map<String, Serializable>) field.get(model);
              if (Arrays.asList(annotation.keys()).isEmpty()){
                for (Map.Entry<String, Serializable> entry: map.entrySet()){
                  String val = entry.getValue().toString();
                  if (val != null && !val.trim().equals("")) {
                    Term term = new Term();
                    term.setTerm(val);
                    term.setModel(model.getClass());
                    term.setField(entry.getKey());
                    term.setReferenceIds(Collections.singletonList(model.getId().toString()));
                    terms.add(term);
                  }
                }
              } else {
                for (String key : annotation.keys()) {
                  if (map.containsKey(key)) {
                    String val = map.get(key).toString();
                    if (val != null && !val.trim().equals("")) {
                      Term term = new Term();
                      term.setTerm(val);
                      term.setModel(model.getClass());
                      term.setField(field.getName());
                      term.setReferenceIds(Collections.singletonList(model.getId().toString()));
                      terms.add(term);
                    }
                  }
                }
              }
            }
          } 
          // Collection fields
          else if (Collection.class.isAssignableFrom(field.getType()) 
              && field.getGenericType() instanceof ParameterizedType){
            Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            if (Serializable.class.isAssignableFrom((Class<?>) args[0])){
              field.setAccessible(true);
              for (Object val: (Collection<?>) field.get(model)){
                if (val != null && !val.toString().trim().equals("")) {
                  Term term = new Term();
                  term.setTerm(val.toString());
                  term.setModel(model.getClass());
                  term.setField(field.getName());
                  term.setReferenceIds(Collections.singletonList(model.getId().toString()));
                  terms.add(term);
                }
              }
            }
          }
          
        }
        
      }
      
      current = current.getSuperclass();
      
    }
    
    return terms;
    
  }
  
  public static boolean modelHasManagedTerms(Class<?> model){
    Class<?> current = model;
    while (current.getSuperclass() != null){
      for (Field field: current.getDeclaredFields()){
        if (field.isAnnotationPresent(ManagedTerm.class)){ 
          return true;
        }
      }
      current = current.getSuperclass();
    }
    return false;
  }

  @Override
  public String toString() {
    return "Term{" +
        "term='" + term + '\'' +
        ", model='" + model + '\'' +
        ", field='" + field + '\'' +
        ", referenceIds=" + referenceIds +
        '}';
  }
}
