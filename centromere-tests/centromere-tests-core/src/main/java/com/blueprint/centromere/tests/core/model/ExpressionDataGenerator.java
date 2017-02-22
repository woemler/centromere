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

package com.blueprint.centromere.tests.core.model;

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.models.GeneExpression;
import com.blueprint.centromere.core.commons.models.Sample;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class ExpressionDataGenerator {
	
	public static List<GeneExpression> generateData(List<Sample> samples, List<Gene> genes, List<DataFile> dataFiles){
		
		List<GeneExpression> data = new ArrayList<>();
		
		GeneExpression geneExpression = new GeneExpression();
		geneExpression.setSample(samples.get(0));
		geneExpression.setGene(genes.get(0));
		geneExpression.setDataFile(dataFiles.get(0));
		geneExpression.setValue(1.23);
		data.add(geneExpression);

		geneExpression = new GeneExpression();
		geneExpression.setSample(samples.get(0));
		geneExpression.setGene(genes.get(1));
		geneExpression.setDataFile(dataFiles.get(0));
		geneExpression.setValue(2.34);
		data.add(geneExpression);

		geneExpression = new GeneExpression();
		geneExpression.setSample(samples.get(0));
		geneExpression.setGene(genes.get(2));
		geneExpression.setDataFile(dataFiles.get(0));
		geneExpression.setValue(4.56);
		data.add(geneExpression);

		geneExpression = new GeneExpression();
		geneExpression.setSample(samples.get(1));
		geneExpression.setGene(genes.get(0));
		geneExpression.setDataFile(dataFiles.get(0));
		geneExpression.setValue(6.78);
		data.add(geneExpression);

		geneExpression = new GeneExpression();
		geneExpression.setSample(samples.get(1));
		geneExpression.setGene(genes.get(1));
		geneExpression.setDataFile(dataFiles.get(0));
		geneExpression.setValue(9.10);
		data.add(geneExpression);

		geneExpression = new GeneExpression();
		geneExpression.setSample(samples.get(1));
		geneExpression.setGene(genes.get(2));
		geneExpression.setDataFile(dataFiles.get(0));
		geneExpression.setValue(12.34);
		data.add(geneExpression);
		
		return data;
	}
	
}
