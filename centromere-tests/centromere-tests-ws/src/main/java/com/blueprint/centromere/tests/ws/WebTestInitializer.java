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

package com.blueprint.centromere.tests.ws;

import com.blueprint.centromere.core.config.AutoConfigureCentromere;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import com.blueprint.centromere.ws.CentromereWebInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author woemler
 */
@AutoConfigureCentromere
@Configuration
@SpringBootApplication
@Import({ MongoDataSourceConfig.class })
public class WebTestInitializer extends CentromereWebInitializer {

  public static void main(String[] args) {
    CentromereWebInitializer.run(WebTestInitializer.class, args);
  }

}