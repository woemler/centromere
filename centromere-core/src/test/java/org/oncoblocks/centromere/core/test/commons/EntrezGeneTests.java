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

package org.oncoblocks.centromere.core.test.commons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.commons.models.Gene;
import org.oncoblocks.centromere.core.commons.readers.EntrezGeneInfoReader;
import org.oncoblocks.centromere.core.test.TestConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class EntrezGeneTests {
	
	@Test
	public void geneInfoReaderTest() throws Exception {
		ClassPathResource resource = new ClassPathResource("Homo_sapiens.gene_info");
		EntrezGeneInfoReader<GeneImpl> reader = new EntrezGeneInfoReader<>(GeneImpl.class);
		Assert.isTrue(GeneImpl.class.equals(reader.getModel()), String.format("Expected %s, got %s",
				GeneImpl.class.getName(), reader.getModel().getName()));
		try {
			reader.doBefore(new String[] {resource.getPath()});
			Gene gene = reader.readRecord();
			Assert.notNull(gene);
			Assert.isTrue(gene instanceof GeneImpl);
			Assert.isTrue("A1BG".equals(gene.getPrimaryGeneSymbol()));
		} finally {
			reader.doAfter();
		}
		
	}
	
}
