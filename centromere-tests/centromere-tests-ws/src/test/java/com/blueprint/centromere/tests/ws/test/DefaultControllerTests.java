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

import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.config.Security;
import com.blueprint.centromere.tests.core.config.EmbeddedMongoConfig;
import com.blueprint.centromere.tests.ws.AbstractControllerTests;
import com.blueprint.centromere.ws.config.WebApplicationConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {
		EmbeddedMongoConfig.class,
		WebApplicationConfig.class
})
@ActiveProfiles(value = { Profiles.WEB_PROFILE, Security.NONE_PROFILE })
public class DefaultControllerTests extends AbstractControllerTests {

	@Autowired private WebApplicationContext context;

	@Before
  @Override
  public void setup() throws Exception {
    super.setup();
    this.setMockMvc(MockMvcBuilders.webAppContextSetup(context).build());
  }
}
