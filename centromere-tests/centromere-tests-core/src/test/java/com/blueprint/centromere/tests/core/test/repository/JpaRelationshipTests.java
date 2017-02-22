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

package com.blueprint.centromere.tests.core.test.repository;

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.DataSet;
import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.models.GeneExpression;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.repositories.DataFileRepository;
import com.blueprint.centromere.core.commons.repositories.DataSetRepository;
import com.blueprint.centromere.core.commons.repositories.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import com.blueprint.centromere.tests.core.config.EmbeddedH2DataSourceConfig;
import com.blueprint.centromere.tests.core.model.DataFileGenerator;
import com.blueprint.centromere.tests.core.model.DataSetGenerator;
import com.blueprint.centromere.tests.core.model.EntrezGeneDataGenerator;
import com.blueprint.centromere.tests.core.model.ExpressionDataGenerator;
import com.blueprint.centromere.tests.core.model.SampleDataGenerator;
import com.blueprint.centromere.tests.core.model.SubjectDataGenerator;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EmbeddedH2DataSourceConfig.class })
public class JpaRelationshipTests {
	
	@Autowired private SampleRepository sampleRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private DataFileRepository dataFileRepository;
	@Autowired private DataSetRepository dataSetRepository;
	@Autowired private GeneRepository geneRepository;
	@Autowired private GeneExpressionRepository geneExpressionRepository;
	private boolean isConfigured = false;

	@Before
	public void setup() throws Exception {
		if (!isConfigured) {
			geneExpressionRepository.deleteAll();
			sampleRepository.deleteAll();
			subjectRepository.deleteAll();
			dataFileRepository.deleteAll();
			dataSetRepository.deleteAll();
			geneRepository.deleteAll();

			List<DataSet> dataSets = DataSetGenerator.generateData();
			dataSetRepository.save(dataSets);
			List<DataFile> dataFiles = DataFileGenerator.generateData(dataSets);
			dataFileRepository.save(dataFiles);
			List<Subject> subjects = SubjectDataGenerator.generateData();
			subjectRepository.save(subjects);
			List<Sample> samples = SampleDataGenerator.generateData(subjects, dataSets.get(0));
			sampleRepository.save(samples);
			List<Gene> genes = EntrezGeneDataGenerator.generateData();
			geneRepository.save(genes);
			List<GeneExpression> data = ExpressionDataGenerator.generateData(samples, genes, dataFiles);
			geneExpressionRepository.save(data);
			isConfigured = true;
		}
	}
	
	@Test
	public void setupTest(){
		Assert.isTrue(sampleRepository.count() == 5L);
		Assert.isTrue(subjectRepository.count() == 5L);
	}

	@Test
	public void manyToOneRelationshipTest() {
		List<Sample> samples = (List<Sample>) sampleRepository.findAll();
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.size() == 5L);
		Sample sample = samples.get(0);
		Assert.notNull(sample.getId());
		Assert.notNull(sample.getSubjectId());
		Assert.notNull(sample.getSubject());
		Assert.notNull(sample.getSubject().getId());
		Assert.isTrue(sample.getSubject().getId().equals(sample.getSubjectId()));
	}

	@Test
	@Transactional(readOnly = true)
	public void oneToManyRelationshipTest(){
		List<Subject> subjects = subjectRepository.findByName("SubjectA");
		Assert.notNull(subjects);
		Assert.notEmpty(subjects);
		Subject subject = subjects.get(0);
		Assert.notNull(subject.getId());
		Assert.notNull(subject.getSamples());
		Assert.notEmpty(subject.getSamples());
		Sample sample = subject.getSamples().get(0);
		Assert.notNull(sample.getId());
		Assert.notNull(sample.getSubjectId());
		Assert.isTrue(sample.getSubjectId().equals(subject.getId()));
	}
	
	@Test
	public void queryByManyToOneRelationshipTest(){
		PathBuilder<Sample> pathBuilder = new PathBuilder<>(Sample.class, "sample");
		SimplePath<Subject> subjectPath = pathBuilder.getSimple("subject", Subject.class);
		StringPath stringPath = Expressions.stringPath(subjectPath, "name");
		Expression constant = Expressions.constant("SubjectB");
		Predicate predicate = Expressions.predicate(Ops.EQ, stringPath, constant);
		List<Sample> samples = (List<Sample>) sampleRepository.findAll(predicate);
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.size() == 2);
		Sample sample = samples.get(0);
		Assert.isTrue("SampleD".equals(sample.getName()));
	}
	
	@Test
	public void queryByOneToManyRelationshipTest(){
		PathBuilder<Subject> pathBuilder = new PathBuilder<>(Subject.class, "subject");
		CollectionPath<Sample, PathBuilder<Sample>> samplePath = pathBuilder.getCollection("samples", Sample.class);
		StringPath stringPath = Expressions.stringPath(samplePath.any(), "tissue");
		Expression constant = Expressions.constant("Breast");
		Predicate predicate = Expressions.predicate(Ops.EQ, stringPath, constant);
		List<Subject> subjects = (List<Subject>) subjectRepository.findAll(predicate);
		Assert.notNull(subjects);
		Assert.notEmpty(subjects);
		Assert.isTrue(subjects.size() == 1);
		Subject subject = subjects.get(0);
		Assert.isTrue("SubjectB".equals(subject.getName()));
	}
	
	@Test
	public void findDataByDataSetTest(){
		PathBuilder<GeneExpression> pathBuilder = new PathBuilder<>(GeneExpression.class, "geneExpression");
		SimplePath<DataFile> dataFilePath = pathBuilder.getSimple("dataFile", DataFile.class);
		SimplePath<DataSet> dataSetPath = Expressions.simplePath(DataSet.class, dataFilePath, "dataSet");
		StringPath stringPath = Expressions.stringPath(dataSetPath, "source");
		Expression constant = Expressions.constant("Internal");
		Predicate predicate = Expressions.predicate(Ops.EQ, stringPath, constant);
		List<GeneExpression> data = (List<GeneExpression>) geneExpressionRepository.findAll(predicate);
		Assert.notNull(data);
		Assert.notEmpty(data);
		Assert.isTrue(data.size() == 6);
		constant = Expressions.constant("External");
		predicate = Expressions.predicate(Ops.EQ, stringPath, constant);
		data = (List<GeneExpression>) geneExpressionRepository.findAll(predicate);
		Assert.notNull(data);
		Assert.isTrue(data.size() == 0);
	}
	
}
