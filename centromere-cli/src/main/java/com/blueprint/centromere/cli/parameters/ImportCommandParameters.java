/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author woemler
 * @since 0.5.0
 */
@Data
@Parameters(commandDescription = "Imports a single file into the data warehouse.")
public class ImportCommandParameters {
  
  public static final String COMMAND = "import";
  public static final String HELP = "Imports a single file into the data warehouse.";
	
	@Parameter(names = { "-f", "--file" }, required = true, description = "Input file path.  Required")
	private String filePath;

	@Parameter(names = { "-t", "--type" }, required = true, 
      description = "Data type label for the target file.  Required.")
	private String dataType;

  @Parameter(names = { "-d", "--data-set" }, required = true, description = "Identifier used to identify " 
      + "the associated data set.  If not specified, the file will not be associated with any data set.")
  private String dataSetId;
	
	@Parameter(names = { "-o", "--overwrite" }, description = "If the target file already exists in the " 
      + "data warehouse, its records will be wiped and the file re-imported.")
  private boolean overwrite = false;

  @Parameter(names = { "-s", "--sample" }, description = "Identifier used to associate all contained " 
      + "file data with a single sample.  Useful if the file contains no sample identifiers, or if the " 
      + "sample identifier is included in the file name.")
  private String sampleId;
	
	@Parameter(names = { "--skip-invalid-records" }, 
      description = "Invalid data records will be skipped, rather than triggering exceptions.")
  private boolean skipInvalidRecords = false;

  @Parameter(names = { "--skip-invalid-genes" },
      description = "Records with invalid gene metadata will be skipped, rather than triggering exceptions.")
  private boolean skipInvalidGenes = false;

  @Parameter(names = { "--skip-invalid-samples" },
      description = "Records with invalid sample metadata will be skipped, rather than triggering exceptions.")
  private boolean skipInvalidSamples = false;

  @Parameter(names = { "--skip-invalid-files" },
      description = "Invalid data files will be skipped, rather than triggering exceptions.")
  private boolean skipInvalidFiles = false;
  
  @Parameter(names = { "-h", "--help" }, description = "Displays usage information, specific to file " 
      + "import operations.")
  private boolean help = false;

  @DynamicParameter(names = "-D", description = "Dynamic parameters, used to assign attributes to " 
      + "the data file record.")
  private Map<String, String> attributes = new HashMap<>();
  
}
