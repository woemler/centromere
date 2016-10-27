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

package com.blueprint.centromere.core.test.model;

import com.blueprint.centromere.core.commons.models.Sample;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class SampleDataGenerator implements DummyDataGenerator<Sample> {
	
	public List<Sample> generateData(Class<Sample> type) throws Exception {
		
		List<Sample> samples = new ArrayList<>();
		
		Sample sample = type.newInstance();
		sample.setName("SampleA");
		sample.setTissue("Lung");
		sample.setHistology("carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagA");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleB");
		sample.setTissue("Liver");
		sample.setHistology("carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagB");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleC");
		sample.setTissue("Liver");
		sample.setHistology("carcinoma: HCC");
		sample.setSampleType("PDX");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagA");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleD");
		sample.setTissue("Breast");
		sample.setHistology("ductal carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagA");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleE");
		sample.setTissue("Breats");
		sample.setHistology("ductal carcinoma");
		sample.setSampleType("PDX");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagB");
		samples.add(sample);
		
		return samples;
		
	}
	
}
