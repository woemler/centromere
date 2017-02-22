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

package com.blueprint.centromere.tests.ws.test;

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
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.core.config.EmbeddedH2DataSourceConfig;
import com.blueprint.centromere.tests.core.model.DataFileGenerator;
import com.blueprint.centromere.tests.core.model.DataSetGenerator;
import com.blueprint.centromere.tests.core.model.EntrezGeneDataGenerator;
import com.blueprint.centromere.tests.core.model.ExpressionDataGenerator;
import com.blueprint.centromere.tests.core.model.SampleDataGenerator;
import com.blueprint.centromere.tests.core.model.SubjectDataGenerator;
import com.blueprint.centromere.ws.config.WebApplicationConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {
		EmbeddedH2DataSourceConfig.class,
		WebApplicationConfig.class
})
@ActiveProfiles({ "default", Profiles.WEB_PROFILE })
public class DefaultControllerRelationshipTests {

	private static final String SUBJECT_URL = "/api/subjects";
	private static final String SAMPLE_URL = "/api/samples";
	private static final String GENE_EXP_URL = "/api/geneexpression";

	@Autowired private WebApplicationContext context;
	@Autowired private SampleRepository sampleRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private DataFileRepository dataFileRepository;
	@Autowired private DataSetRepository dataSetRepository;
	@Autowired private GeneRepository geneRepository;
	@Autowired private GeneExpressionRepository geneExpressionRepository;

	private MockMvc mockMvc;
	private boolean isConfigured = false;

	@Before
	public void setup() throws Exception {
		if (!isConfigured) {
			doDelete();
			doInsert();
			isConfigured = true;
		}
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	private void doDelete(){
		geneExpressionRepository.deleteAll();
		geneRepository.deleteAll();
		sampleRepository.deleteAll();
		subjectRepository.deleteAll();
		dataFileRepository.deleteAll();
		dataSetRepository.deleteAll();
	}
	
	private void doInsert() throws Exception{

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
	}

	@Test
	public void findAllSubjectsTest() throws Exception {
		mockMvc.perform(get(SUBJECT_URL))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("subjects")))
				.andExpect(jsonPath("$._embedded.subjects", hasSize(5)))
				.andExpect(jsonPath("$._embedded.subjects[0]", hasKey("name")))
				.andExpect(jsonPath("$._embedded.subjects[0].name", is("SubjectA")))
				.andExpect(jsonPath("$._embedded.subjects[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.subjects[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.subjects[0].aliases[0]", is("subject_a")))
				.andExpect(jsonPath("$._embedded.subjects[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.subjects[0].attributes", hasKey("tag")))
				.andExpect(jsonPath("$._embedded.subjects[0].attributes.tag", is("tagA")))
				.andExpect(jsonPath("$._embedded.subjects[0]", not(hasKey("samples"))))
				.andExpect(jsonPath("$._embedded.subjects[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.subjects[0]._links", hasKey("self")));
	}

	@Test
	public void findAllSamplesTest() throws Exception {
		mockMvc.perform(get(SAMPLE_URL))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("samples")))
				.andExpect(jsonPath("$._embedded.samples", hasSize(5)))
				.andExpect(jsonPath("$._embedded.samples[0]", hasKey("name")))
				.andExpect(jsonPath("$._embedded.samples[0].name", is("SampleA")))
				.andExpect(jsonPath("$._embedded.samples[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.samples[0].attributes", hasKey("tag")))
				.andExpect(jsonPath("$._embedded.samples[0].attributes.tag", is("tagA")))
				.andExpect(jsonPath("$._embedded.samples[0]", not(hasKey("subject"))))
				.andExpect(jsonPath("$._embedded.samples[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$._embedded.samples[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.samples[0]._links", hasKey("self")));
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void findSampleSubjectTest() throws Exception {
		
		Sample sample = ((List<Sample>) sampleRepository.findAll()).get(0);
		Assert.notNull(sample);
		Assert.notNull(sample.getId());
		Assert.notNull(sample.getSubjectId());

		mockMvc.perform(get(SAMPLE_URL + "/{id}", sample.getId()))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("name")))
				.andExpect(jsonPath("$.name", is("SampleA")))
				.andExpect(jsonPath("$", hasKey("subjectId")))
				.andExpect(jsonPath("$.subjectId", is(sample.getSubjectId().toString())))
				.andExpect(jsonPath("$", hasKey("_links")))
				.andExpect(jsonPath("$._links", hasKey("self")))
				.andExpect(jsonPath("$._links", hasKey("subject")));

		Subject subject = subjectRepository.findOne(sample.getSubjectId());
		Assert.notNull(subject);
		Assert.isTrue(subject.getId().equals(sample.getSubjectId()));

		mockMvc.perform(get(SAMPLE_URL + "/{id}/subject", sample.getId()))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("name")))
				.andExpect(jsonPath("$.name", is("SubjectA")))
				.andExpect(jsonPath("$", hasKey("_links")))
				.andExpect(jsonPath("$._links", hasKey("self")));

	}

//	@Test
//	public void findSubjectSamplesTest() throws Exception {
//		
//		Subject subject = ((List<Subject>) subjectRepository.findAll()).get(0);
//		Assert.notNull(subject);
//		Assert.notNull(subject.getId());
//
//		mockMvc.perform(get(SUBJECT_URL + "/{id}", subject.getId()))
//				.andExpect(status().isOk())
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(jsonPath("$", hasKey("name")))
//				.andExpect(jsonPath("$.name", is("SubjectA")))
//				.andExpect(jsonPath("$", hasKey("_links")))
//				.andExpect(jsonPath("$._links", hasKey("self")))
//				.andExpect(jsonPath("$._links", hasKey("samples")));
//
//		List<Sample> samples = sampleRepository.findBySubjectId(subject.getId());
//		Assert.notNull(samples);
//		Assert.notEmpty(samples);
//		Assert.isTrue(samples.size() == 3);
//
//		mockMvc.perform(get(SUBJECT_URL + "/{id}/samples", subject.getId()))
//				.andExpect(status().isOk())
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(jsonPath("$", hasKey("_embedded")))
//				.andExpect(jsonPath("$._embedded", hasKey("samples")))
//				.andExpect(jsonPath("$._embedded.samples", hasSize(3)))
//				.andExpect(jsonPath("$._embedded.samples[0]", hasKey("name")))
//				.andExpect(jsonPath("$._embedded.samples[0].name", is("SampleA")))
//				.andExpect(jsonPath("$", hasKey("_links")))
//				.andExpect(jsonPath("$._links", hasKey("self")))
//				.andExpect(jsonPath("$._links.self", hasKey("href")))
//				.andExpect(jsonPath("$._links.self.href", endsWith("samples")));
//
//	}

	@Test
	public void findSubjectFilteredRelatedSamplesTest() throws Exception {
		mockMvc.perform(get(SUBJECT_URL + "?samples.tissue=Breast"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("subjects")))
				.andExpect(jsonPath("$._embedded.subjects", hasSize(1)))
				.andExpect(jsonPath("$._embedded.subjects[0]", hasKey("name")))
				.andExpect(jsonPath("$._embedded.subjects[0].name", is("SubjectB")));
	}

	@Test
	public void findSamplesFilteredRelatedSubjectTest() throws Exception {
		mockMvc.perform(get(SAMPLE_URL + "?subject.name=SubjectB"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("samples")))
				.andExpect(jsonPath("$._embedded.samples", hasSize(2)))
				.andExpect(jsonPath("$._embedded.samples[0]", hasKey("name")))
				.andExpect(jsonPath("$._embedded.samples[0].name", is("SampleD")));
	}

	@Test
	public void findAllDataTest() throws Exception {
		mockMvc.perform(get(GENE_EXP_URL))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("geneExpression")))
				.andExpect(jsonPath("$._embedded.geneExpression", hasSize(6)))
				.andExpect(jsonPath("$._embedded.geneExpression[0]", hasKey("value")))
				.andExpect(jsonPath("$._embedded.geneExpression[0].value", is(1.23)));
	}

	@Test
	public void findDataByGeneSymbol() throws Exception {
		mockMvc.perform(get(GENE_EXP_URL + "?gene.primaryGeneSymbol=GeneB"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("geneExpression")))
				.andExpect(jsonPath("$._embedded.geneExpression", hasSize(2)))
				.andExpect(jsonPath("$._embedded.geneExpression[0]", hasKey("value")))
				.andExpect(jsonPath("$._embedded.geneExpression[0].value", is(2.34)));
	}

	@Test
	public void findDataBySubjectName() throws Exception {
		mockMvc.perform(get(GENE_EXP_URL + "?sample.subject.name=SubjectA"))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("geneExpression")))
				.andExpect(jsonPath("$._embedded.geneExpression", hasSize(6)))
				.andExpect(jsonPath("$._embedded.geneExpression[0]", hasKey("value")))
				.andExpect(jsonPath("$._embedded.geneExpression[0].value", is(1.23)));
	}

}
