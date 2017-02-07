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

package com.blueprint.centromere.core.test.commons;

import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.readers.TcgaSubjectReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SubjectTests {
	
	@Test
	public void tcgaSubjectReaderTest() throws Exception {
		ClassPathResource resource = new ClassPathResource("samples/tcga_sample_subjects.txt");
		TcgaSubjectReader reader = new TcgaSubjectReader();
		Assert.isTrue(Subject.class.equals(reader.getModel()), String.format("Expected %s, got %s",
				Subject.class.getName(), reader.getModel().getName()));
        List<Subject> subjects = new ArrayList<>();
		try {
			reader.doBefore(new String[] {resource.getPath()});
			Subject subject = reader.readRecord();
            while (subject != null){
                subjects.add(subject);
                subject = reader.readRecord();
            }
		} finally {
			reader.doAfter();
		}
		Assert.notEmpty(subjects);
        System.out.println(subjects.toString());
        Assert.isTrue(subjects.size() == 5);
        Subject subject = subjects.get(0);
        Assert.notNull(subject);
        Assert.isTrue("tcga-2y-a9gv".equals(subject.getName()));
        Assert.isTrue("liver".equals(subject.getAttribute("tumor_tissue_site")));
        subject = subjects.get(4);
        Assert.notNull(subject);
        Assert.isTrue("tcga-bc-a110".equals(subject.getName()));
        Assert.isTrue("black or african american".equals(subject.getAttribute("race")));
	}
	
}