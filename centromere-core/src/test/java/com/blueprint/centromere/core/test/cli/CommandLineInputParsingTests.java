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

package com.blueprint.centromere.core.test.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;
import com.blueprint.centromere.core.dataimport.cli.CommandLineInputExecutor;
import com.blueprint.centromere.core.dataimport.cli.ImportCommandArguments;
import com.blueprint.centromere.core.dataimport.cli.ImportFileCommandArguments;
import com.blueprint.centromere.core.dataimport.cli.ImportManifestCommandArguments;
import com.blueprint.centromere.core.test.jpa.EmbeddedH2DataSourceConfig;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EmbeddedH2DataSourceConfig.class })
@FixMethodOrder
public class CommandLineInputParsingTests {
	
	private JCommander jc;
	private ImportCommandArguments importCommandArguments;
	private ImportFileCommandArguments importFileCommandArguments;
	private ImportManifestCommandArguments importManifestCommandArguments;
	private JCommander importJc;
	
	@Before
	public void setup(){
		jc = new JCommander();
		importCommandArguments = new ImportCommandArguments();
		importFileCommandArguments = new ImportFileCommandArguments();
		importManifestCommandArguments = new ImportManifestCommandArguments();
		jc.addCommand(CommandLineInputExecutor.IMPORT_COMMAND, importCommandArguments);
		importJc = jc.getCommands().get(CommandLineInputExecutor.IMPORT_COMMAND);
		importJc.addCommand(CommandLineInputExecutor.IMPORT_FILE_COMMAND, importFileCommandArguments);
		importJc.addCommand(CommandLineInputExecutor.IMPORT_BATCH_COMMAND, importManifestCommandArguments);
	}
	
	@Test
	public void invalidMainCommandTest() throws Exception {
		Exception exception = null;
		String[] args = {"bad", "args", "-Dparam=true"};
		try {
			jc.parse(args);
		} catch (Exception e){
			exception = e;
			e.printStackTrace();
		}
		Assert.notNull(exception);
		Assert.isTrue(exception instanceof MissingCommandException);
	}

	@Test
	public void invalidSubCommandTest() throws Exception {
		Exception exception = null;
		String[] args = {"import", "bad", "args", "-Dparam=true"};
		try {
			jc.parse(args);
		} catch (Exception e){
			exception = e;
			e.printStackTrace();
		}
		Assert.notNull(exception);
		Assert.isTrue(exception instanceof MissingCommandException);
	}
	
	@Test
	public void genericArgumentParsingTest() throws Exception {
		String[] args = { "import", "-DskipInvalidGenes=true", "-DdefaultValue=2", "-DdefaultText=this is a test" };
		jc.parse(args);
		Assert.isTrue("import".equals(jc.getParsedCommand()));
		Assert.notNull(importCommandArguments.getParameters());
		Assert.notEmpty(importCommandArguments.getParameters());
		Assert.isTrue("true".equals(importCommandArguments.getParameters().get("skipInvalidGenes")));
		Assert.isTrue("this is a test".equals(importCommandArguments.getParameters().get("defaultText")));
		Assert.isTrue("2".equals(importCommandArguments.getParameters().get("defaultValue")));
	}
	
	@Test
	public void importFileCommandParsingTest() throws Exception {
		String[] args = { "import", "file", "-f", "/path/to/file.txt", "--type", "test_type" };
		jc.parse(args);
		Assert.isTrue("import".equals(jc.getParsedCommand()));
		Assert.isTrue("file".equals(importJc.getParsedCommand()));
		Assert.isTrue("/path/to/file.txt".equals(importFileCommandArguments.getFilePath()));
		Assert.isTrue("test_type".equals(importFileCommandArguments.getDataType()));
	}

	@Test
	public void importBatchCommandParsingTest() throws Exception {
		String[] args = { "import", "batch", "-f", "/path/to/file.txt" };
		jc.parse(args);
		Assert.isTrue("import".equals(jc.getParsedCommand()));
		Assert.isTrue("batch".equals(importJc.getParsedCommand()));
		Assert.isTrue("/path/to/file.txt".equals(importManifestCommandArguments.getFilePath()));
	}

	@Test
	public void badArgumentsTest() throws Exception {
		String[] args = { "import", "batch", "-f", "/path/to/file.txt", "-d", "my-data" };
		Exception exception = null;
		try {
			jc.parse(args);
		} catch (Exception e){
			exception = e;
			e.printStackTrace();
		}
		Assert.notNull(exception);
		Assert.isTrue(exception instanceof ParameterException);
	}
	
}
