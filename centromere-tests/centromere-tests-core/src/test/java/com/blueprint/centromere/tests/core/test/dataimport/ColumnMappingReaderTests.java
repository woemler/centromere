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

package com.blueprint.centromere.tests.core.test.dataimport;

import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.dataimport.impl.readers.BasicColumnMappingRecordReader;
import com.blueprint.centromere.core.model.Model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ColumnMappingReaderTests {
	
	private Resource exampleFile = new ClassPathResource("samples/example_mapped_data.txt");
	
	@Test
	public void mappedReaderTest() throws Exception {
		BasicColumnMappingRecordReader<ExampleData> reader = new BasicColumnMappingRecordReader<>();
		reader.setModel(ExampleData.class);
		List<ExampleData> records = new ArrayList<>();
		try {
			reader.doBefore(exampleFile.getFile().getAbsolutePath());

			ExampleData record = reader.readRecord();
			while (record != null) {
				records.add(record);
				record = reader.readRecord();
			}
		} finally {
			reader.doAfter();
		}
		Assert.notEmpty(records);
		Assert.isTrue(records.size() == 5);
		Assert.isTrue("Joe".equals(records.get(0).getColumnA()));
		Assert.isTrue(records.get(0).getColumnB().equals(34));
		Assert.isTrue(records.get(0).getColumnC().equals(3.123));
		Assert.notNull(records.get(0).getColumnD());
		Assert.notEmpty(records.get(0).getColumnD());
		Assert.isTrue(records.get(0).getColumnD().size() == 2);
		Assert.isTrue("bbb".equals(records.get(0).getColumnD().get(1)));
		Assert.isTrue("Mary".equals(records.get(1).getColumnA()));
		Assert.isTrue(records.get(1).getColumnB().equals(45));
		Assert.isTrue(records.get(1).getColumnC().equals(4.0));
		Assert.isNull(records.get(1).getColumnD());
		Assert.isTrue("Henry".equals(records.get(2).getColumnA()));
		Assert.isTrue(records.get(2).getColumnB().equals(12));
		Assert.isNull(records.get(2).getColumnC());
		Assert.notNull(records.get(2).getColumnD());
		Assert.notEmpty(records.get(2).getColumnD());
		Assert.isTrue(records.get(2).getColumnD().size() == 1);
		Assert.isTrue("stuff".equals(records.get(2).getColumnD().get(0)));
	}
	
	@Test
	public void modelBeanWrapperTest() throws Exception {
		ConversionService conversionService = new DefaultConversionService();
		BeanWrapperImpl wrapper = new BeanWrapperImpl(Gene.class);
		String entrezGeneId = "7";
		String geneSymbol = "TEST";
		String taxId = "9606";
		String aliases = "AAA,BBB,CCC";
		String xrefs = "db1=ABC;db2=DEF";
		
		Class<?> type = wrapper.getPropertyType("primaryGeneSymbol");
		Object value = geneSymbol;
		if (!type.equals(String.class)){
			if (conversionService.canConvert(type, String.class)){
				value = conversionService.convert(value, type);
			} else {
				System.out.println(String.format("Cannot convert %s to String", type.getName()));
			}
		}
		wrapper.setPropertyValue("primaryGeneSymbol", value);

		type = wrapper.getPropertyType("primaryReferenceId");
		value = entrezGeneId;
		if (!type.equals(String.class)){
			if (conversionService.canConvert(type, String.class)){
				value = conversionService.convert(value, type);
			} else {
				System.out.println(String.format("Cannot convert %s to String", type.getName()));
			}
		}
		wrapper.setPropertyValue("primaryReferenceId", value);

		type = wrapper.getPropertyType("taxId");
		value = taxId;
		if (!type.equals(String.class)){
			if (conversionService.canConvert(type, String.class)){
				value = conversionService.convert(value, type);
			} else {
				System.out.println(String.format("Cannot convert %s to String", type.getName()));
			}
		}
		wrapper.setPropertyValue("taxId", value);

		type = wrapper.getPropertyType("aliases");
		value = aliases;
		if (!type.equals(String.class)){
			if (conversionService.canConvert(type, String.class)){
				value = conversionService.convert(value, type);
			} else {
				System.out.println(String.format("Cannot convert %s to String", type.getName()));
			}
		}
		wrapper.setPropertyValue("aliases", value);

//		type = wrapper.getPropertyType("dbXrefs");
//		value = xrefs;
//		if (!type.equals(String.class)){
//			if (conversionService.canConvert(type, String.class)){
//				value = conversionService.convert(value, type);
//			} else {
//				System.out.println(String.format("Cannot convert %s to String", type.getName()));
//			}
//		}
//		wrapper.setPropertyValue("dbXrefs", value);
		
		Gene gene = (Gene) wrapper.getWrappedInstance();
		Assert.notNull(gene);
		Assert.notNull(gene.getPrimaryGeneSymbol());
		Assert.isTrue("TEST".equals(gene.getPrimaryGeneSymbol()));
		Assert.notNull(gene.getPrimaryReferenceId());
		Assert.isTrue(gene.getPrimaryReferenceId() == "7");
		Assert.notNull(gene.getTaxId());
		Assert.isTrue(gene.getTaxId() == 9606);
		Assert.notNull(gene.getAliases());
		Assert.notEmpty(gene.getAliases());
		Assert.isTrue(gene.getAliases().size() == 3);
//		Assert.notNull(gene.getDbXrefs());
//		Assert.notEmpty(gene.getDbXrefs());
//		Assert.isTrue("DEF".equals(gene.getDbXrefs().get("db2")));
		
		System.out.println(gene.toString());

		
	}

	public static class ExampleData implements Model<String> {

		private String id;
		private String columnA;
		private Integer columnB;
		private Double columnC;
		private List<String> columnD;

		@Override public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getColumnA() {
			return columnA;
		}

		public void setColumnA(String columnA) {
			this.columnA = columnA;
		}

		public Integer getColumnB() {
			return columnB;
		}

		public void setColumnB(Integer columnB) {
			this.columnB = columnB;
		}

		public Double getColumnC() {
			return columnC;
		}

		public void setColumnC(Double columnC) {
			this.columnC = columnC;
		}

		public List<String> getColumnD() {
			return columnD;
		}

		public void setColumnD(List<String> columnD) {
			this.columnD = columnD;
		}
	}
	
}
