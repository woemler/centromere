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

package org.oncoblocks.centromere.mongodb.test.commons;

import com.blueprint.centromere.core.commons.repositories.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.commons.repositories.*;
import com.blueprint.centromere.core.commons.testing.DataFileGenerator;
import com.blueprint.centromere.core.commons.testing.DataSetGenerator;
import com.blueprint.centromere.core.commons.testing.SampleDataGenerator;
import com.blueprint.centromere.core.commons.testing.SubjectDataGenerator;
import org.oncoblocks.centromere.mongodb.commons.models.*;
import org.oncoblocks.centromere.mongodb.commons.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { 
		MongoCommonsDataSourceTestConfig.class, 
		MongoCommonsRepositoryTestConfig.class 
})
public class MongoCommonsRepositoryTests {
	
	@Autowired private GeneRepository geneRepository;
	@Autowired private SampleRepository sampleRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private DataSetRepository dataSetRepository;
	@Autowired private DataFileRepository dataFileRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private UserDetailsService userDetailsService;
	
	@Before
	public void setup(){
		
	}
	
	@Test
	public void userDetailsRepositoryTest() throws Exception {
		Assert.notNull(userRepository);
		Assert.notNull(userDetailsService);
		MongoUserRepository repo1 = (MongoUserRepository) userRepository;
		MongoUserRepository repo2 = (MongoUserRepository) userDetailsService;
		Assert.isTrue(repo1.equals(repo2));
	}

	@Test
	public void dataSetRepositoryTest() throws Exception {

		Assert.notNull(dataSetRepository);
		Assert.isTrue(dataSetRepository instanceof MongoDataSetRepository);
		Assert.isTrue(dataSetRepository.count() == 0);
		Assert.isTrue(dataSetRepository.getModel().equals(MongoDataSet.class));

		List<MongoDataSet> records = new DataSetGenerator<MongoDataSet>().generateData(MongoDataSet.class);
		dataSetRepository.insert(records);
		Assert.isTrue(dataSetRepository.count() == 5);

		records = dataSetRepository.findByName("DataSetB");
		Assert.notNull(records);
		Assert.notEmpty(records);
		Assert.isTrue(records.size() == 1);
		Assert.isTrue(records.get(0).getSource().equals("External"));

		records = dataSetRepository.findBySource("Internal");
		Assert.notNull(records);
		Assert.notEmpty(records);
		Assert.isTrue(records.size() == 3);
		Assert.isTrue(records.get(0).getName().equals("DataSetA"));

	}

	@Test
	public void dataFileRepositoryTest() throws Exception {

		Assert.notNull(dataFileRepository);
		Assert.isTrue(dataFileRepository instanceof MongoDataFileRepository);
		Assert.isTrue(dataFileRepository.count() == 0);
		Assert.isTrue(dataFileRepository.getModel().equals(MongoDataFile.class));

		List<MongoDataFile> records = new DataFileGenerator<MongoDataFile>().generateData(MongoDataFile.class);
		dataFileRepository.insert(records);
		Assert.isTrue(dataFileRepository.count() == 5);

		records = dataFileRepository.findByFilePath("/path/to/fileA");
		Assert.notNull(records);
		Assert.notEmpty(records);
		Assert.isTrue(records.get(0).getDataType().equals("GCT RNA-Seq gene expression"));

		records = dataFileRepository.findByDataType("MAF mutations");
		Assert.notNull(records);
		Assert.notEmpty(records);
		Assert.isTrue(records.size() == 1);
		Assert.isTrue(records.get(0).getFilePath().equals("/path/to/fileC"));
		
	}

	@Test
	public void subjectRepositoryTest() throws Exception {

		Assert.notNull(subjectRepository);
		Assert.isTrue(subjectRepository instanceof MongoSubjectRepository);
		Assert.isTrue(subjectRepository.count() == 0);
		Assert.isTrue(subjectRepository.getModel().equals(MongoSubject.class));

		List<MongoSubject> records = new SubjectDataGenerator<MongoSubject>().generateData(MongoSubject.class);
		subjectRepository.insert(records);
		Assert.isTrue(subjectRepository.count() == 5);

		records = subjectRepository.findByName("SubjectA");
		Assert.notNull(records);
		Assert.notEmpty(records);
		Assert.isTrue(records.get(0).getGender().equals("M"));

		records = subjectRepository.findByAlias("subject_e");
		Assert.notNull(records);
		Assert.notEmpty(records);
		Assert.isTrue(records.size() == 1);
		Assert.isTrue(records.get(0).getName().equals("SubjectE"));

		records = subjectRepository.findByAttribute("tag", "tagB");
		Assert.notNull(records);
		Assert.notEmpty(records);
		Assert.isTrue(records.size() == 2);
		Assert.isTrue(records.get(0).getName().equals("SubjectC"));
	}
	
	@Test
	public void sampleRepositoryTest() throws Exception {
		
		Assert.notNull(sampleRepository);
		Assert.isTrue(sampleRepository instanceof MongoSampleRepository);
		Assert.isTrue(sampleRepository.count() == 0);
		Assert.isTrue(sampleRepository.getModel().equals(MongoSample.class));
		
		List<MongoSample> samples = new SampleDataGenerator<MongoSample>().generateData(MongoSample.class);
		sampleRepository.insert(samples);
		Assert.isTrue(sampleRepository.count() == 5);
		
		samples = sampleRepository.findByName("SampleA");
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.get(0).getTissue().equals("Lung"));

		samples = sampleRepository.findByHistology("carcinoma");
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.size() == 2);
		Assert.isTrue(samples.get(0).getName().equals("SampleA"));

		samples = sampleRepository.findByTissue("Liver");
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.size() == 2);
		Assert.isTrue(samples.get(0).getName().equals("SampleB"));

		samples = sampleRepository.findBySampleType("PDX");
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.size() == 2);
		Assert.isTrue(samples.get(0).getName().equals("SampleC"));

		samples = sampleRepository.findByAlias("sample_e");
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.size() == 1);
		Assert.isTrue(samples.get(0).getName().equals("SampleE"));

		samples = sampleRepository.findByAttribute("tag", "tagB");
		Assert.notNull(samples);
		Assert.notEmpty(samples);
		Assert.isTrue(samples.size() == 2);
		Assert.isTrue(samples.get(0).getName().equals("SampleB"));
	}
	
	@Test
	public void geneRepositoryTest() throws Exception {
		Assert.notNull(geneRepository);
		Assert.isTrue(geneRepository instanceof MongoGeneRepository);
		Assert.isTrue(geneRepository.count() == 0);
		Assert.isTrue(geneRepository.getModel().equals(MongoGene.class), 
				String.format("Expected %s, got %s", MongoGene.class.getName(), geneRepository.getModel().getName()));
		MongoGene gene = new MongoGene();
		gene.setPrimaryReferenceId("0");
		gene.setPrimaryGeneSymbol("TEST");
		gene.setTaxId(9606);
		gene.addAlias("alias");
		geneRepository.insert(gene);
		Assert.isTrue(geneRepository.count() == 1, 
				String.format("Expected 1, found %d", geneRepository.count()));
		List<MongoGene> genes = geneRepository.findByPrimaryReferenceId("0");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.get(0).getPrimaryReferenceId().equals("0"));
		genes = geneRepository.guessGene("alias");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.get(0).getPrimaryGeneSymbol().equals("TEST"));
	}
	
	
}
