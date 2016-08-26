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
import org.oncoblocks.centromere.core.config.DataTypeProcessorBeanRegistry;
import org.oncoblocks.centromere.core.config.ModelRegistry;
import org.oncoblocks.centromere.core.config.ModelRepositoryBeanRegistry;
import org.oncoblocks.centromere.core.config.ModelScan;
import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

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
		List<Class<? extends Model>> models = modelRegistry.getModels();
		Assert.notNull(models);
		Assert.notEmpty(models);
		Assert.isTrue(models.size() > 0);
	}

	private ModelRegistry createModelRegistry() throws Exception {
		ModelRegistry modelRegistry = new ModelRegistry();
		if (this.getClass().isAnnotationPresent(ModelScan.class)){
			ModelScan modelScan = this.getClass().getAnnotation(ModelScan.class);
			printModelScan(modelScan);
			if (modelScan.value() != null && modelScan.value().length > 0) {
				for (String path : modelScan.value()) {
					modelRegistry.addClassPathModels(path);
				}
			} else if (modelScan.basePackages() != null && modelScan.basePackages().length > 0) {
				for (String path : modelScan.basePackages()) {
					modelRegistry.addClassPathModels(path);
				}
			}
			if (modelScan.basePackageClasses() != null && modelScan.basePackageClasses().length > 0){
				for (Class<?> clazz: modelScan.basePackageClasses()){
					if (Model.class.isAssignableFrom(clazz)){
						modelRegistry.addModel((Class<? extends Model>) clazz);
					}
				}
			}
		}
		modelRegistry.setProcessorRegistry(new DataTypeProcessorBeanRegistry(context));
		modelRegistry.setRepositoryRegistry(new ModelRepositoryBeanRegistry(context));
		modelRegistry.afterPropertiesSet();
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
		ModelRegistry modelRegistry = createModelRegistry();
		DataTypeProcessorBeanRegistry registry = (DataTypeProcessorBeanRegistry) modelRegistry.getProcessorRegistry();
		modelRegistry.setProcessorRegistry(new DataTypeProcessorBeanRegistry(context));
		Assert.notNull(registry);
		Assert.isTrue(registry.isSupportedDataType("gene_info"));
		RecordProcessor processor = registry.getByDataType("gene_info");
		Assert.notNull(processor);
		Assert.isTrue(processor instanceof GeneInfoProcessor);
	}

	@Test
	public void modelRepositoryRegistryTest() throws Exception {
		ModelRepositoryBeanRegistry registry = (ModelRepositoryBeanRegistry) createModelRegistry().getRepositoryRegistry();
		Assert.notNull(registry);
		Assert.isTrue(registry.isSupported(EntrezGene.class));
		RepositoryOperations repository = registry.get(EntrezGene.class);
		Assert.notNull(repository);
		Assert.isTrue(repository instanceof TestRepository);
	}
	
}
