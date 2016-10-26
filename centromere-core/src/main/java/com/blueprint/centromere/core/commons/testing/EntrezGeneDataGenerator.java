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

package com.blueprint.centromere.core.commons.testing;

import com.blueprint.centromere.core.commons.models.Gene;

import java.util.ArrayList;
import java.util.List;

/**
 * Support class for consistently generating example {@link Gene} objects for testing.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class EntrezGeneDataGenerator<T extends Gene<?>> implements DummyDataGenerator<T> {
	
	public List<T> generateData(Class<T> type) throws Exception {

		List<T> genes = new ArrayList<>();
		
		T gene = type.newInstance();
		gene.setPrimaryReferenceId("1");
		gene.setPrimaryGeneSymbol("GeneA");
		gene.setTaxId(9606);
		gene.setChromosome("1");
		gene.setDescription("Test Gene A");
		gene.setGeneType("protein-coding");
		gene.addAttribute("isKinase","Y");
		gene.addAlias("ABC");
		genes.add(gene);

		gene = type.newInstance();
		gene.setPrimaryReferenceId("2");
		gene.setPrimaryGeneSymbol("GeneB");
		gene.setTaxId(9606);
		gene.setChromosome("3");
		gene.setDescription("Test Gene B");
		gene.setGeneType("protein-coding");
		gene.addAttribute("isKinase", "N");
		gene.addAlias("DEF");
		genes.add(gene);

		gene = type.newInstance();
		gene.setPrimaryReferenceId("3");
		gene.setPrimaryGeneSymbol("GeneC");
		gene.setTaxId(9606);
		gene.setChromosome("3");
		gene.setDescription("Test Gene C");
		gene.setGeneType("pseudo");
		gene.addAttribute("isKinase", "N");
		gene.addAlias("GHI");
		genes.add(gene);
		
		gene = type.newInstance();
		gene.setPrimaryReferenceId("4");
		gene.setPrimaryGeneSymbol("GeneD");
		gene.setTaxId(9606);
		gene.setChromosome("9");
		gene.setDescription("Test Gene D");
		gene.setGeneType("protein-coding");
		gene.addAttribute("isKinase", "Y");
		gene.addAlias("JKL");
		genes.add(gene);
		
		gene = type.newInstance();
		gene.setPrimaryReferenceId("5");
		gene.setPrimaryGeneSymbol("GeneE");
		gene.setTaxId(9606);
		gene.setChromosome("X");
		gene.setDescription("Test Gene E");
		gene.setGeneType("pseudo");
		gene.addAttribute("isKinase", "N");
		gene.addAlias("MNO");
		genes.add(gene);
		
		return genes;
	}
	
}
