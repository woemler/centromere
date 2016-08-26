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

package org.oncoblocks.centromere.core.commons.testing;

import org.oncoblocks.centromere.core.commons.models.Sample;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class SampleDataGenerator<T extends Sample<?>> implements DummyDataGenerator<T> {
	
	public List<T> generateData(Class<T> type) throws Exception {
		
		List<T> samples = new ArrayList<>();
		
		T sample = type.newInstance();
		sample.setName("SampleA");
		sample.setTissue("Lung");
		sample.setHistology("carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAlias("sample_a");
		sample.addAttribute("tag", "tagA");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleB");
		sample.setTissue("Liver");
		sample.setHistology("carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAlias("sample_b");
		sample.addAttribute("tag", "tagB");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleC");
		sample.setTissue("Liver");
		sample.setHistology("carcinoma: HCC");
		sample.setSampleType("PDX");
		sample.setNotes("This is an example sample.");
		sample.addAlias("sample_c");
		sample.addAttribute("tag", "tagA");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleD");
		sample.setTissue("Breast");
		sample.setHistology("ductal carcinoma");
		sample.setSampleType("cell line");
		sample.setNotes("This is an example sample.");
		sample.addAlias("sample_d");
		sample.addAttribute("tag", "tagA");
		samples.add(sample);

		sample = type.newInstance();
		sample.setName("SampleE");
		sample.setTissue("Breats");
		sample.setHistology("ductal carcinoma");
		sample.setSampleType("PDX");
		sample.setNotes("This is an example sample.");
		sample.addAlias("sample_e");
		sample.addAttribute("tag", "tagB");
		samples.add(sample);
		
		return samples;
		
	}
	
}
