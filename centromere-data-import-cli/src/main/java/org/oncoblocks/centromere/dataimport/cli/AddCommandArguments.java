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

package org.oncoblocks.centromere.dataimport.cli;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command line argument configuration for the {@code add} command.  The {@code add} command expects
 *   three arguments: a category, label, and object body.  The category refers to the type of 
 *   record to be added.  The label is a unique string identifier that will be shorthand reference
 *   to the added record, used in the {@link DataImportManager}.  The body can be a complete JSON
 *   representation of the added object, a reference to a bean or class. 
 * 
 * @author woemler
 */
public class AddCommandArguments {
	
	@Parameter(description = "Positional argument format example: java -jar import.jar category label body")
	private List<String> args = new ArrayList<>();

	@DynamicParameter(names = "-D", description = "Dynamic key-value parameters. eg -Dname=Joe")
	private Map<String, String> parameters = new HashMap<>();

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public String getCategory() {
		return args.size() > 0 ? args.get(0) : null;
	}

	public String getLabel() {
		return args.size() > 1 ? args.get(1) : null;
	}

	public String getBody() {
		return args.size() > 2 ? args.get(2) : null;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	@Override 
	public String toString() {
		return "AddCommandArguments{" +
				"args=" + args +
				", category='" + (this.getCategory() != null ? this.getCategory() : "") + '\'' +
				", label='" + (this.getLabel() != null ? this.getLabel() : "") + '\'' +
				", body='" + (this.getBody() != null ? this.getBody() : "") + '\'' +
				'}';
	}
}
