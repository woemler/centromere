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

package com.blueprint.centromere.tests.cli.test;

import com.blueprint.centromere.cli.CommandLineInputConfiguration;
import com.blueprint.centromere.cli.manifest.ImportManifest;
import com.blueprint.centromere.cli.manifest.ManifestFile;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.core.config.EmbeddedMongoConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { 
		EmbeddedMongoConfig.class,
		CommandLineInputConfiguration.class,
		CommandLineTestConfig.class
})
@ActiveProfiles({Profiles.CLI_PROFILE})
@FixMethodOrder
public class ManifestParsingTests {
	
	@Before
	public void setup(){
	}

	private void testManifestObject(ImportManifest manifest){
		Assert.notNull(manifest);
		System.out.println(manifest.toString());
        Assert.notNull(manifest.getAttributes());
        Assert.notEmpty(manifest.getAttributes());
		Assert.isTrue("Test Manifest".equals(manifest.getAttributes().get("name")));
		Assert.isTrue("test".equals(manifest.getAttributes().get("label")));
		Assert.isTrue("This is a test".equals(manifest.getAttributes().get("notes")));
		Assert.isTrue("internal".equals(manifest.getAttributes().get("source")));
		Assert.isTrue("1.0".equals(manifest.getAttributes().get("version")));
		Assert.notNull(manifest.getAttributes().get("date-created"));
		Assert.isTrue(manifest.getAttributes().containsKey("skip-invalid-records"));
		Assert.isTrue("true".equals(manifest.getAttributes().get("skip-invalid-records")));
		Assert.notNull(manifest.getFiles());
		Assert.notEmpty(manifest.getFiles());
		Assert.isTrue(manifest.getFiles().size() == 1);
		ManifestFile manifestFile = manifest.getFiles().get(0);
		Assert.notNull(manifestFile);
		Assert.notNull(manifestFile.getType());
		Assert.notNull(manifestFile.getPath());
		Assert.isTrue("../Homo_sapiens.gene_info".equals(manifestFile.getPath()));
		Assert.isTrue("gene-info".equals(manifestFile.getType()));
	}
	
	@Test
	public void yamlParsingTest() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		ClassPathResource resource = new ClassPathResource("samples/example_manifest.yml");
		File file = resource.getFile();
		ImportManifest manifest = objectMapper.readValue(file, ImportManifest.class);
		testManifestObject(manifest);
	}
	
	@Test
	public void jsonParsingTest() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ClassPathResource resource = new ClassPathResource("samples/example_manifest.json");
		File file = resource.getFile();
		ImportManifest manifest = objectMapper.readValue(file, ImportManifest.class);
		testManifestObject(manifest);
	}

	@Test
	public void xmlParsingTest() throws Exception {
		ObjectMapper objectMapper = new XmlMapper();
		ClassPathResource resource = new ClassPathResource("samples/example_manifest.xml");
		File file = resource.getFile();
		ImportManifest manifest = objectMapper.readValue(file, ImportManifest.class);
		testManifestObject(manifest);
	}
	
}
