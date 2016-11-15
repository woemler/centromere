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

package com.blueprint.centromere.core.test.jpa;

import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import com.blueprint.centromere.core.test.model.SampleDataGenerator;
import com.blueprint.centromere.core.test.model.SubjectDataGenerator;

import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EmbeddedH2DataSourceConfig.class })
public class JpaRelationshipTests {
	
	@Autowired private SampleRepository sampleRepository;
	@Autowired private SubjectRepository subjectRepository;

	@Before
	public void setup() throws Exception {
		
		sampleRepository.deleteAll();
		subjectRepository.deleteAll();

		List<Subject> subjects = SubjectDataGenerator.generateData();
		subjectRepository.save(subjects);

		List<Sample> samples = SampleDataGenerator.generateData(subjects);
		sampleRepository.save(samples);

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
	
}
