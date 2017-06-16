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

package com.blueprint.centromere.core.dataimport.writer;

import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.model.Model;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.FileWriter;
import java.io.IOException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
public class MongoImportTempFileWriter<T extends Model<?>> extends AbstractRecordFileWriter<T> {

  private final MongoOperations mongoOperations;

  public MongoImportTempFileWriter(Class<T> model, MongoOperations mongoOperations) {
    super(model);
    this.mongoOperations = mongoOperations;
  }

  /**
   * Empty default implementation.  The purpose of extending {@link org.springframework.beans.factory.InitializingBean} 
   * is to trigger bean post-processing by a {@link BeanPostProcessor}.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    Assert.notNull(mongoOperations, "MongoOperations must not be null");
  }

  /**
   * Writes a {@link com.blueprint.centromere.core.model.Model} record to a temp file, formatted into JSON.
   *
   * @param record
   */
  public void writeRecord(T record)  {
    FileWriter writer = this.getWriter();
    try {
      writer.write(convertEntityToJson(record));
      writer.write("\n");
    } catch (IOException e){
      throw new DataImportException(e);
    }
  }

  private String convertEntityToJson(Object entity){
    MongoConverter converter = mongoOperations.getConverter();
    DBObject dbObject = new BasicDBObject();
    converter.write(entity, dbObject);
    if (dbObject.containsField("_id") && dbObject.get("_id") == null){
      dbObject.removeField("_id");
    }
    if (dbObject.containsField("_class")){
      dbObject.removeField("_class");
    }
    return dbObject.toString();
  }

  public MongoOperations getMongoOperations() {
    return mongoOperations;
  }

}
