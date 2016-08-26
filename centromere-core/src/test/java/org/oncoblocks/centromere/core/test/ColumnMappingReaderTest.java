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

package org.oncoblocks.centromere.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class ColumnMappingReaderTest {
	
	@Test
	public void modelBeanWrapperTest() throws Exception {
		ConversionService conversionService = new DefaultConversionService();
		BeanWrapperImpl wrapper = new BeanWrapperImpl(EntrezGene.class);
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

		type = wrapper.getPropertyType("entrezGeneId");
		value = entrezGeneId;
		if (!type.equals(String.class)){
			if (conversionService.canConvert(type, String.class)){
				value = conversionService.convert(value, type);
			} else {
				System.out.println(String.format("Cannot convert %s to String", type.getName()));
			}
		}
		wrapper.setPropertyValue("entrezGeneId", value);

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
		
		EntrezGene gene = (EntrezGene) wrapper.getWrappedInstance();
		Assert.notNull(gene);
		Assert.notNull(gene.getPrimaryGeneSymbol());
		Assert.isTrue("TEST".equals(gene.getPrimaryGeneSymbol()));
		Assert.notNull(gene.getEntrezGeneId());
		Assert.isTrue(gene.getEntrezGeneId() == 7L);
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
	
}
