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
 *   two arguments: {@link org.oncoblocks.centromere.core.model.Model} class name and model object 
 *   representation.  The model class is identified using a full class name or shorthand alias, which
 *   is registered by a {@link org.oncoblocks.centromere.core.util.ModelRegistry} instance.  The 
 *   body can be a complete JSON representation of the target class, or represented as key-value
 *   pairs using dynamic parameter arguments. 
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

	public String getType() {
		return args.size() > 0 ? args.get(0) : null;
	}

	public String getBody() {
		return args.size() > 1 ? args.get(1) : null;
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
				", type='" + (this.getType() != null ? this.getType() : "") + '\'' +
				", body='" + (this.getBody() != null ? this.getBody() : "") + '\'' +
				'}';
	}
}
