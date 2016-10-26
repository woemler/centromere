/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.test.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author woemler
 */
public class ModelTestUtil {

	public static List<Gene> createDummyGeneData(){

		List<Gene> genes = new ArrayList<>();

		Gene gene = new Gene();
		gene.setEntrezGeneId(1L);
		gene.setPrimaryGeneSymbol("GeneA");
		gene.setTaxId(9606);
		gene.setChromosome("1");
		gene.setGeneType("protein-coding");
		gene.setAttributes(Collections.singletonMap("isKinase", "Y"));
		gene.setAliases(Collections.singletonList("ABC"));
		genes.add(gene);

		gene = new Gene();
		gene.setEntrezGeneId(2L);
		gene.setPrimaryGeneSymbol("GeneB");
		gene.setTaxId(9606);
		gene.setChromosome("5");
		gene.setGeneType("protein-coding");
		gene.setAttributes(Collections.singletonMap("isKinase", "N"));
		gene.setAliases(Collections.singletonList("DEF"));
		genes.add(gene);

		gene = new Gene();
		gene.setEntrezGeneId(3L);
		gene.setPrimaryGeneSymbol("GeneC");
		gene.setTaxId(9606);
		gene.setChromosome("9");
		gene.setGeneType("pseudo");
		gene.setAttributes(Collections.singletonMap("isKinase", "N"));
		gene.setAliases(Collections.singletonList("GHI"));
		genes.add(gene);

		gene = new Gene();
		gene.setEntrezGeneId(4L);
		gene.setPrimaryGeneSymbol("GeneD");
		gene.setTaxId(9606);
		gene.setChromosome("X");
		gene.setGeneType("protein-coding");
		gene.setAttributes(Collections.singletonMap("isKinase", "Y"));
		gene.setAliases(Collections.singletonList("JKL"));
		genes.add(gene);

		gene = new Gene();
		gene.setEntrezGeneId(5L);
		gene.setPrimaryGeneSymbol("GeneE");
		gene.setTaxId(9606);
		gene.setChromosome("13");
		gene.setGeneType("protein-coding");
		gene.setAttributes(Collections.singletonMap("isKinase", "N"));
		gene.setAliases(Collections.singletonList("MNO"));
		genes.add(gene);

		return genes;

	}
	
}
