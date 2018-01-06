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

package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.reader.GenericSampleReader;
import com.blueprint.centromere.core.commons.reader.TcgaSampleReader;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.mongodb.model.MongoDataFile;
import com.blueprint.centromere.core.mongodb.model.MongoDataSet;
import com.blueprint.centromere.core.mongodb.model.MongoSample;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SampleTests {

  @Autowired private Environment environment;

	@Test
	public void tcgaSubjectReaderTest() throws Exception {
    ClassPathResource resource = new ClassPathResource("samples/tcga_sample_subjects.txt");
	  DataSet dataSet = new MongoDataSet();
		dataSet.setDataSetId("test");
		dataSet.setName("Test");
		dataSet.setId("test");
		DataFile dataFile = new MongoDataFile();
		dataFile.setFilePath(resource.getPath());
		dataFile.setDataSetId((String) dataSet.getId());
		dataFile.setId("test");
		TcgaSampleReader<MongoSample> reader = new TcgaSampleReader<>(MongoSample.class);
		reader.setDataSet(dataSet);
		reader.setDataFile(dataFile);
		Assert.isTrue(MongoSample.class.equals(reader.getModel()), String.format("Expected %s, got %s",
    Sample.class.getName(), reader.getModel().getName()));
    List<MongoSample> samples = new ArrayList<>();
		try {
			reader.doBefore();
      MongoSample sample = reader.readRecord();
      while (sample != null){
          samples.add(sample);
          sample = reader.readRecord();
      }
		} finally {
			reader.doAfter();
		}
		Assert.notEmpty(samples);
    System.out.println(samples.toString());
    Assert.isTrue(samples.size() == 5);
    Sample sample = samples.get(0);
    Assert.notNull(sample);
    Assert.isTrue("tcga-2y-a9gv".equals(sample.getSampleId()));
    Assert.isTrue("liver".equals(sample.getAttribute("tumor_tissue_site")));
    sample = samples.get(4);
    Assert.notNull(sample);
    Assert.isTrue("tcga-bc-a110".equals(sample.getSampleId()));
    Assert.isTrue("black or african american".equals(sample.getAttribute("race")));
	}
	
	@Test
  public void genericSampleReaderTest() throws Exception {
	  ClassPathResource resource = new ClassPathResource("samples/cell_lines.txt");
    DataSet dataSet = new MongoDataSet();
    dataSet.setDataSetId("test");
    dataSet.setName("Test");
    dataSet.setId("test");
    DataFile dataFile = new MongoDataFile();
    dataFile.setFilePath(resource.getPath());
    dataFile.setDataSetId((String) dataSet.getId());
    dataFile.setId("test");
    GenericSampleReader<MongoSample> reader = new GenericSampleReader<>(MongoSample.class, new DataImportProperties());
    reader.setDataFile(dataFile);
    reader.setDataSet(dataSet);
    reader.setDelimiter(",");
    Assert.isTrue(MongoSample.class.equals(reader.getModel()), String.format("Expected %s, got %s",
        Sample.class.getName(), reader.getModel().getName()));
    
    List<MongoSample> samples = new ArrayList<>();
    try {
      reader.doBefore();
      MongoSample sample = reader.readRecord();
      while (sample != null){
        samples.add(sample);
        sample = reader.readRecord();
      }
    } finally {
      reader.doAfter();
    }
    
    Assert.notEmpty(samples);
    System.out.println(samples.toString());
    Assert.isTrue(samples.size() == 5);
    
    Sample sample = samples.get(0);
    Assert.notNull(sample);
    System.out.println(sample);
    Assert.notNull(sample.getSampleId());
    Assert.isTrue("105KC_BONE".equals(sample.getSampleId()));
    Assert.notNull(sample.getName());
    Assert.isTrue("105KC_BONE".equals(sample.getName()));
    Assert.notNull(sample.getTissue());
    Assert.isTrue("bone".equals(sample.getTissue()));
    Assert.notNull(sample.getHistology());
    Assert.isTrue("chondrosarcoma".equals(sample.getHistology()));
    Assert.notNull(sample.getGender());
    Assert.isTrue("n/a".equals(sample.getGender()));
    Assert.notNull(sample.getAliases());
    Assert.isTrue(!sample.getAliases().isEmpty());
    Assert.isTrue("105KC".equals(sample.getAliases().get(0)));
    Assert.notNull(sample.getAttributes());
    Assert.isTrue(!sample.getAttributes().isEmpty());
    Assert.isTrue(sample.hasAttribute("example_attribute"));
    Assert.isTrue("test".equals(sample.getAttribute("example_attribute")));

    sample = samples.get(1);
    Assert.notNull(sample);
    System.out.println(sample);
    Assert.notNull(sample.getSampleId());
    Assert.isTrue("1321N1_CNS".equals(sample.getSampleId()));
    Assert.notNull(sample.getName());
    Assert.isTrue("1321N1_CNS".equals(sample.getName()));
    Assert.notNull(sample.getTissue());
    Assert.isTrue("central_nervous_system".equals(sample.getTissue()));
    Assert.notNull(sample.getHistology());
    Assert.isTrue("glioma: astrocytoma".equals(sample.getHistology()));
    Assert.notNull(sample.getGender());
    Assert.isTrue("M".equals(sample.getGender()));
    Assert.notNull(sample.getAliases());
    Assert.isTrue(!sample.getAliases().isEmpty());
    Assert.isTrue("1321N1".equals(sample.getAliases().get(0)));
    Assert.notNull(sample.getAttributes());
    Assert.isTrue(!sample.getAttributes().isEmpty());
    Assert.isTrue(sample.hasAttribute("example_attribute"));
    Assert.isTrue("test".equals(sample.getAttribute("example_attribute")));
    
  }
	
}
