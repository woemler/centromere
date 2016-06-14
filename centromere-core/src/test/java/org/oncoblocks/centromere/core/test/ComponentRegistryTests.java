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

package org.oncoblocks.centromere.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.oncoblocks.centromere.core.util.DataTypeProcessorRegistry;
import org.oncoblocks.centromere.core.util.ModelRegistry;
import org.oncoblocks.centromere.core.util.ModelRepositoryRegistry;
import org.oncoblocks.centromere.core.util.ModelScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ModelScan(basePackages = { "org.oncoblocks.centromere.core.test" })
@ContextConfiguration(classes = { TestConfig.class })
public class ComponentRegistryTests {
	
	@Autowired private ApplicationContext context;
	
	@Test
	public void modelScannerTest(){
		ModelScan modelScan = this.getClass().getAnnotation(ModelScan.class);
		Assert.notNull(modelScan);
		Assert.isTrue(modelScan.basePackages() != null);
		Assert.isTrue(modelScan.basePackages().length == 1);
		Assert.isTrue("org.oncoblocks.centromere.core.test".equals(modelScan.basePackages()[0]));
	}
	
	@Test
	public void modelRegistryTest() throws Exception {
		ModelRegistry modelRegistry = createModelRegistry();
		Assert.notNull(modelRegistry);
		Map<String, Class<? extends Model>> models = modelRegistry.getRegistry();
		Assert.notNull(models);
		Assert.notEmpty(models);
		Assert.isTrue(models.size() == 2);
	}

	private ModelRegistry createModelRegistry() throws Exception {
		ModelRegistry modelRegistry = new ModelRegistry();
		if (this.getClass().isAnnotationPresent(ModelScan.class)){
			ModelScan modelScan = this.getClass().getAnnotation(ModelScan.class);
			printModelScan(modelScan);
			if (modelScan.value() != null && modelScan.value().length > 0) {
				for (String path : modelScan.value()) {
					modelRegistry.addClasspathModels(path);
				}
			} else if (modelScan.basePackages() != null && modelScan.basePackages().length > 0) {
				for (String path : modelScan.basePackages()) {
					modelRegistry.addClasspathModels(path);
				}
			}
			if (modelScan.basePackageClasses() != null && modelScan.basePackageClasses().length > 0){
				for (Class<?> clazz: modelScan.basePackageClasses()){
					if (Model.class.isAssignableFrom(clazz)){
						modelRegistry.add((Class<? extends Model>) clazz);
					}
				}
			}
		}
		return modelRegistry;
	}
	
	private void printModelScan(ModelScan modelScan){
		if (modelScan.value() != null){
			System.out.println("Value: ");
			for (String val: modelScan.value()){
				System.out.println("  " + val);
			}
		}
		if (modelScan.basePackages() != null){
			System.out.println("Base Packages: ");
			for (String val: modelScan.basePackages()){
				System.out.println("  " + val);
			}
		}
		if (modelScan.basePackageClasses() != null){
			System.out.println("Base Package Classes: ");
			for (Class<?> val: modelScan.basePackageClasses()){
				System.out.println("  " + val.getName());
			}
		}
	}
	
	@Test
	public void dataTypeProcessorRegistryTest() throws Exception {
		DataTypeProcessorRegistry registry = new DataTypeProcessorRegistry(context);
		registry.configure();
		Assert.notNull(registry);
		Assert.notEmpty(registry.getRegistry());
		Assert.isTrue(registry.getRegistry().size() == 1);
		Assert.isTrue(registry.exists("gene_info"));
		RecordProcessor processor = registry.find("gene_info");
		Assert.notNull(processor);
		Assert.isTrue(processor instanceof GeneInfoProcessor);
	}

	@Test
	public void modelRepositoryRegistryTest() throws Exception {
		ModelRepositoryRegistry registry = new ModelRepositoryRegistry(context);
		registry.configure();
		Assert.notNull(registry);
		Assert.notEmpty(registry.getRegistry());
		Assert.isTrue(registry.getRegistry().size() == 1);
		Assert.isTrue(registry.exists(EntrezGene.class));
		List<RepositoryOperations> repositories = registry.findByModel(EntrezGene.class);
		Assert.notNull(repositories);
		Assert.notEmpty(repositories);
		Assert.isTrue(repositories.size() == 1);
		RepositoryOperations repository = repositories.get(0);
		Assert.isTrue(repository instanceof TestRepository);
	}
	
}
