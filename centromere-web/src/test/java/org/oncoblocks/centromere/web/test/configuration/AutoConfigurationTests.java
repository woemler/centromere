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

package org.oncoblocks.centromere.web.test.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.web.config.Profiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
		AutoConfigSetup.DefaultAutoConfig.class
})
@ActiveProfiles({Profiles.SCHEMA_MONGODB_DEFAULT, Profiles.DB_MONGODB})
public class AutoConfigurationTests {
	
	@Autowired private ApplicationContext context;
	
	@Test
	public void configLocatorTest(){
		Map<String, Object> beans =  context.getBeansWithAnnotation(Configuration.class);
		Assert.notEmpty(beans);
		for (Map.Entry entry: beans.entrySet()){
			System.out.println(String.format("Name=%s   Bean=%s", entry.getKey(), entry.getValue().getClass().getName()));
		}
	}
	
	
}
