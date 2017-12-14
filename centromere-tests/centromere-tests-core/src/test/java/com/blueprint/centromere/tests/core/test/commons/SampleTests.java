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
import com.blueprint.centromere.core.commons.reader.TcgaSampleReader;
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
    Sample subject = samples.get(0);
    Assert.notNull(subject);
    Assert.isTrue("tcga-2y-a9gv".equals(subject.getSampleId()));
    Assert.isTrue("liver".equals(subject.getAttribute("tumor_tissue_site")));
    subject = samples.get(4);
    Assert.notNull(subject);
    Assert.isTrue("tcga-bc-a110".equals(subject.getSampleId()));
    Assert.isTrue("black or african american".equals(subject.getAttribute("race")));
	}
	
}
