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

package com.blueprint.centromere.core.test.configuration;

import com.blueprint.centromere.core.config.Database;
import com.blueprint.centromere.core.config.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		AutoConfigSetup.DefaultAutoConfig.class
})
@ActiveProfiles({Schema.DEFAULT_PROFILE, Database.MONGODB_PROFILE})
public class AutoConfigurationTests {
	
	@Test
	public void emptyTest(){
		
	}
	
//	@Autowired private ApplicationContext context;
//	
//	@Test
//	public void configLocatorTest(){
//		Map<String, Object> beans =  context.getBeansWithAnnotation(Configuration.class);
//		Assert.notEmpty(beans);
//		for (Map.Entry entry: beans.entrySet()){
//			System.out.println(String.format("Name=%s   Bean=%s", entry.getKey(), entry.getValue().getClass().getName()));
//		}
//	}
//	
//	@Test
//	public void profilesFromAnnotationTest() throws Exception {
//		Class<?> cfg = AutoConfigSetup.DefaultAutoConfig.class;
//		AutoConfigureCentromere annotation = null;
//		if (cfg.isAnnotationPresent(AutoConfigureCentromere.class)){
//			annotation = cfg.getAnnotation(AutoConfigureCentromere.class);
//		}
//		Assert.notNull(annotation);
//		Assert.notNull(annotation.database());
//		Assert.isTrue(Database.MONGODB.equals(annotation.database()));
//		Assert.notNull(annotation.schema());
//		Assert.isTrue(Schema.DEFAULT.equals(annotation.schema()));
//		String[] profiles = Profiles.getApplicationProfiles(annotation.database(), annotation.schema());
//		Assert.notNull(profiles);
//		Assert.isTrue("db_mongodb".equals(profiles[0]));
//		Assert.isTrue("schema_mongodb".equals(profiles[1]));
//	}
//	
//	@Test
//	public void metaAnnotationTest() throws Exception {
//		Class<?> cfg = AutoConfigSetup.DefaultAutoConfig.class;
//		Assert.isTrue(AnnotatedElementUtils.isAnnotated(cfg, AutoConfigureCentromere.class));
//		AutoConfigureCentromere autoConf = AnnotationUtils.getAnnotation(cfg, AutoConfigureCentromere.class);
//		Assert.notNull(autoConf);
//		Assert.notNull(autoConf.modelClasses());
//		Assert.notEmpty(autoConf.modelClasses());
//		Assert.isTrue(ExampleModel.class.equals(autoConf.modelClasses()[0]));
//		Assert.isTrue(AnnotatedElementUtils.isAnnotated(cfg, ModelScan.class));
//		ModelScan modelScan = AnnotatedElementUtils.findMergedAnnotation(cfg, ModelScan.class);
//		modelScan = AnnotationUtils.synthesizeAnnotation(modelScan, cfg);
//		Assert.notNull(modelScan.modelClasses());
//		Assert.notEmpty(modelScan.modelClasses());
//		Assert.isTrue(ExampleModel.class.equals(modelScan.modelClasses()[0]));
//		
//	}
	
}
