/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

import com.blueprint.centromere.core.dataimport.reader.impl.EntrezGeneInfoReader;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.Gene;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EntrezGeneTests {

  private DataSet dataSet;
  private DataSource dataSource;

  @Before
  public void setup(){
    dataSet = new DataSet();
    dataSet.setDataSetId("test");
    dataSet.setName("Test");
    dataSet.setDataSetId("test");
    dataSource = new DataSource();
    dataSource.setDataSourceId("test");
    dataSource.setDataSetId(dataSet.getDataSetId());
  }
	
	@Test
	public void geneInfoReaderTest() throws Exception {
		ClassPathResource resource = new ClassPathResource("samples/Homo_sapiens.gene_info");
		dataSource.setSource(resource.getPath());
		EntrezGeneInfoReader reader = new EntrezGeneInfoReader();
		reader.setDataSet(dataSet);
		reader.setDataSource(dataSource);
		Assert.isTrue(Gene.class.equals(reader.getModel()), String.format("Expected %s, got %s",
				Gene.class.getName(), reader.getModel().getName()));
		try {
			reader.doBefore();
			Gene gene = reader.readRecord();
			Assert.notNull(gene);
			System.out.println(gene.toString());
			Assert.isTrue(gene instanceof Gene);
			Assert.isTrue("A1BG".equals(gene.getSymbol()));
		} finally {
			reader.doAfter();
		}
		
	}
	
}
