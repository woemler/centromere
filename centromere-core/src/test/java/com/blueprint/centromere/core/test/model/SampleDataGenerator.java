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
import com.blueprint.centromere.core.commons.models.Subject;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class SampleDataGenerator {
	
	public static List<Sample> generateData(List<Subject> subjects) throws Exception {

		Assert.isTrue(subjects.size() > 1);

		List<Sample> samples = new ArrayList<>();
		
		Sample sample = new Sample();
		sample.setName("SampleA");
		sample.setTissue("Lung");
		sample.setHistology("carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagA");
		sample.setSubject(subjects.get(0));
		samples.add(sample);

		sample = new Sample();
		sample.setName("SampleB");
		sample.setTissue("Liver");
		sample.setHistology("carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagB");
		sample.setSubject(subjects.get(0));
		samples.add(sample);

		sample = new Sample();
		sample.setName("SampleC");
		sample.setTissue("Liver");
		sample.setHistology("carcinoma: HCC");
		sample.setSampleType("PDX");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagA");
		sample.setSubject(subjects.get(0));
		samples.add(sample);

		sample = new Sample();
		sample.setName("SampleD");
		sample.setTissue("Breast");
		sample.setHistology("ductal carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagA");
		sample.setSubject(subjects.get(1));
		samples.add(sample);

		sample = new Sample();
		sample.setName("SampleE");
		sample.setTissue("Breast");
		sample.setHistology("ductal carcinoma");
		sample.setSampleType("PDX");
		sample.setNotes("This is an example sample.");
		sample.addAttribute("tag", "tagB");
		sample.setSubject(subjects.get(1));
		samples.add(sample);
		
		return samples;
		
	}
	
}
