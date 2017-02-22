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

package com.blueprint.centromere.tests.ws.test;

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.DataSet;
import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.models.GeneExpression;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.config.Security;
import com.blueprint.centromere.tests.core.config.EmbeddedH2DataSourceConfig;
import com.blueprint.centromere.tests.core.model.DataFileGenerator;
import com.blueprint.centromere.tests.core.model.DataSetGenerator;
import com.blueprint.centromere.tests.core.model.EntrezGeneDataGenerator;
import com.blueprint.centromere.tests.core.model.ExpressionDataGenerator;
import com.blueprint.centromere.tests.core.model.SampleDataGenerator;
import com.blueprint.centromere.tests.core.model.SubjectDataGenerator;
import com.blueprint.centromere.tests.ws.AbstractControllerTests;
import com.blueprint.centromere.ws.config.WebApplicationConfig;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {
		EmbeddedH2DataSourceConfig.class,
		WebApplicationConfig.class
})
@ActiveProfiles(value = { Profiles.WEB_PROFILE, Security.NONE_PROFILE })
public class JpaDefaultControllerTests extends AbstractControllerTests {

	@Autowired private WebApplicationContext context;

	@Before
	public void setup() throws Exception {
		this.setMockMvc(MockMvcBuilders.webAppContextSetup(context).build());
		this.getGeneExpressionRepository().deleteAll();
		this.getSampleRepository().deleteAll();
		this.getSubjectRepository().deleteAll();
		this.getDataFileRepository().deleteAll();
		this.getDataSetRepository().deleteAll();
		this.getGeneRepository().deleteAll();

		List<DataSet> dataSets = DataSetGenerator.generateData();
		this.getDataSetRepository().save(dataSets);
		List<DataFile> dataFiles = DataFileGenerator.generateData(dataSets);
		this.getDataFileRepository().save(dataFiles);
		List<Subject> subjects = SubjectDataGenerator.generateData();
		this.getSubjectRepository().save(subjects);
		List<Sample> samples = SampleDataGenerator.generateData(subjects, dataSets.get(0));
		this.getSampleRepository().save(samples);
		List<Gene> genes = EntrezGeneDataGenerator.generateData();
		this.getGeneRepository().save(genes);
		List<GeneExpression> data = ExpressionDataGenerator.generateData(samples, genes, dataFiles);
		this.getGeneExpressionRepository().save(data);
	}

}
