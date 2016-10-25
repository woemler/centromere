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

package org.oncoblocks.centromere.dataimport.cli.test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.blueprint.centromere.core.config.DataTypeProcessorBeanRegistry;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.model.Model;
import org.oncoblocks.centromere.dataimport.cli.AddCommandArguments;
import org.oncoblocks.centromere.dataimport.cli.AddCommandRunner;
import org.oncoblocks.centromere.dataimport.cli.ImportCommandArguments;
import org.oncoblocks.centromere.dataimport.cli.test.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, TestMongoConfig.class })
public class AddCommandTests {
	
	@Autowired private ApplicationContext context;
	@Autowired private DataSetRepository dataSetRepository;
	@Autowired private DataFileRepository dataFileRepository;
	private DataTypeProcessorBeanRegistry dataTypeProcessorBeanRegistry;
	@Autowired private AddCommandRunner addCommandRunner;
	
	@Before
	public void setup() throws Exception {
		dataFileRepository.deleteAll();
		dataSetRepository.deleteAll();
		dataTypeProcessorBeanRegistry = new DataTypeProcessorBeanRegistry(context);
		dataTypeProcessorBeanRegistry.addModelBeans(Arrays.asList(DataFile.class, DataSet.class,
				SampleData.class));
	}
	
	@Test
	public void addDataSetArgumentsTest() throws Exception {
		AddCommandArguments addCommandArguments = new AddCommandArguments();
		JCommander jCommander = new JCommander();
		jCommander.addCommand("add", addCommandArguments);
		String[] args = { "add", "DataSet", "\"{ \"source\": \"internal\", \"name\": \"Test Data Set\", \"notes\": \"This is a test data set\" }\"",
				"-Dparam1=test", "-Dparam2=TEST"};
		jCommander.parse(args);
		Assert.isTrue("add".equals(jCommander.getParsedCommand()));
		Assert.isTrue(addCommandArguments.getArgs().size() == 2);
		String type = addCommandArguments.getType();
		String body = addCommandArguments.getBody();
		Assert.notNull(type);
		Assert.notNull(body);
		Assert.isTrue("DataSet".equals(type));
		Map<String, String> params = addCommandArguments.getParameters();
		Assert.notNull(params);
		Assert.notEmpty(params);
		Assert.isTrue(params.containsKey("param1"));
		Assert.isTrue("test".equals(params.get("param1")));
		Assert.isTrue(params.containsKey("param2"));
		Assert.isTrue("TEST".equals(params.get("param2")));
	}
	
	@Test
	public void dataTypeMappingTest() throws Exception {
		Assert.isTrue(dataTypeProcessorBeanRegistry.isSupportedDataType("sample_data"));
		Assert.isTrue(dataTypeProcessorBeanRegistry.getByDataType("sample_data") instanceof SampleDataProcessor);
	}
	
	@Test
	public void addDataSetRunnerTest() throws Exception {
		Assert.isTrue(dataSetRepository.count() == 0);
		JCommander commander = new JCommander();
		AddCommandArguments arguments = new AddCommandArguments();
		commander.addCommand("add", arguments);
		String[] args = { "add", "DataSet", "\"{ \"source\": \"internal\", \"name\": \"Test Data Set\" }\"" };
		commander.parse(args);
		addCommandRunner.run(arguments);
		Assert.isTrue(dataSetRepository.count() == 1);
		DataSet dataSet = dataSetRepository.findAll().get(0);
		Assert.notNull(dataSet);
		Assert.notNull(dataSet.getId());
		Assert.isTrue("internal".equals(dataSet.getSource()));
		Assert.isTrue("Test Data Set".equals(dataSet.getName()));
	}

	@Test
	public void badModelAttributesTest() throws Exception {
		Assert.isTrue(dataSetRepository.count() == 0);
		JCommander commander = new JCommander();
		AddCommandArguments arguments = new AddCommandArguments();
		commander.addCommand("add", arguments);
		String[] args = { "add", "DataSet", "\"{ \"source\": \"internal\", \"name\": \"Test Data Set\", \"notes\": \"This is a test data set\" }\"" };
		commander.parse(args);
		Exception exception = null;
		try {
			addCommandRunner.run(arguments);
		} catch (Exception e){
			exception = e;
		}
		Assert.notNull(exception);
		Assert.isTrue(exception instanceof DataImportException);
	}
	
	@Test
	public void badCommandTest() throws Exception {
		JCommander commander = new JCommander();
		AddCommandArguments addCommandArguments = new AddCommandArguments();
		ImportCommandArguments importCommandArguments = new ImportCommandArguments();
		commander.addCommand("add", addCommandArguments);
		commander.addCommand("import", importCommandArguments);
		String[] args = {"bad", "command"};
		Exception exception = null;
		try {
			commander.parse(args);
		} catch (Exception e){
			exception = e;
		}
		Assert.notNull(exception);
		Assert.isTrue(exception instanceof MissingCommandException);
	}

	@Test
	public void badModelTest() throws Exception {
		JCommander commander = new JCommander();
		AddCommandArguments addCommandArguments = new AddCommandArguments();
		commander.addCommand("add", addCommandArguments);
		String[] args = {"add", "bad", "{}"};
		commander.parse(args);
		Exception exception = null;
		try {
			addCommandRunner.run(addCommandArguments);
		} catch (Exception e){
			exception = e;
			e.printStackTrace();
		}
		Assert.notNull(exception);
		Assert.isTrue(exception instanceof DataImportException);
	}
	
	@Test
	public void modelScanningTest() throws Exception {
		ClassPathScanningCandidateComponentProvider provider
				= new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AssignableTypeFilter(Model.class));
		for (BeanDefinition beanDef : provider.findCandidateComponents("org.oncoblocks.centromere.dataimport.cli.test.support")) {
			System.out.println(beanDef.getBeanClassName());
		}
	}
	
}
