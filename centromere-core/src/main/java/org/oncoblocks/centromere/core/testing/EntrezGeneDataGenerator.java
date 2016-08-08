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

package org.oncoblocks.centromere.core.testing;

import org.oncoblocks.centromere.core.commons.EntrezGene;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class EntrezGeneDataGenerator {
	
	public static <T extends EntrezGene<?>> List<T> generateDummyData(Class<T> type) throws Exception {

		List<T> genes = new ArrayList<>();
		
		T gene = type.newInstance();
		gene.setEntrezGeneId(1L);
		gene.setPrimaryGeneSymbol("GeneA");
		gene.setTaxId(9606);
		gene.setChromosome("1");
		gene.setDescription("Test Gene A");
		gene.setGeneType("protein-coding");
		gene.addAttribute("isKinase","Y");
		gene.addGeneSymbolAlias("ABC");
		genes.add(gene);

		gene = type.newInstance();
		gene.setEntrezGeneId(2L);
		gene.setPrimaryGeneSymbol("GeneB");
		gene.setTaxId(9606);
		gene.setChromosome("3");
		gene.setDescription("Test Gene B");
		gene.setGeneType("protein-coding");
		gene.addAttribute("isKinase", "N");
		gene.addGeneSymbolAlias("DEF");
		genes.add(gene);

		gene = type.newInstance();
		gene.setEntrezGeneId(3L);
		gene.setPrimaryGeneSymbol("GeneC");
		gene.setTaxId(9606);
		gene.setChromosome("3");
		gene.setDescription("Test Gene C");
		gene.setGeneType("pseudo");
		gene.addAttribute("isKinase", "N");
		gene.addGeneSymbolAlias("GHI");
		genes.add(gene);
		
		gene = type.newInstance();
		gene.setEntrezGeneId(4L);
		gene.setPrimaryGeneSymbol("GeneD");
		gene.setTaxId(9606);
		gene.setChromosome("9");
		gene.setDescription("Test Gene D");
		gene.setGeneType("protein-coding");
		gene.addAttribute("isKinase", "Y");
		gene.addGeneSymbolAlias("JKL");
		genes.add(gene);
		
		gene = type.newInstance();
		gene.setEntrezGeneId(5L);
		gene.setPrimaryGeneSymbol("GeneE");
		gene.setTaxId(9606);
		gene.setChromosome("X");
		gene.setDescription("Test Gene E");
		gene.setGeneType("pseudo");
		gene.addAttribute("isKinase", "N");
		gene.addGeneSymbolAlias("MNO");
		genes.add(gene);
		
		return genes;
	}
	
}
