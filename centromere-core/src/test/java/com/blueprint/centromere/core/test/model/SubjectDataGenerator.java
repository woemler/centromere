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

import com.blueprint.centromere.core.commons.models.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class SubjectDataGenerator {

	public static List<Subject> generateData() throws Exception {
		
		List<Subject> subjects = new ArrayList<>();
		
		Subject subject = new Subject();
		subject.setName("SubjectA");
		subject.setSpecies("Human");
		subject.setGender("M");
		subject.setNotes("This is an example subject");
		subject.addAlias("subject_a");
		subject.addAttribute("tag", "tagA");
		subjects.add(subject);

		subject = new Subject();
		subject.setName("SubjectB");
		subject.setSpecies("Human");
		subject.setGender("F");
		subject.setNotes("This is an example subject");
		subject.addAlias("subject_b");
		subject.addAttribute("tag", "tagA");
		subjects.add(subject);

		subject = new Subject();
		subject.setName("SubjectC");
		subject.setSpecies("Mouse");
		subject.setGender("M");
		subject.setNotes("This is an example subject");
		subject.addAlias("subject_c");
		subject.addAttribute("tag", "tagB");
		subjects.add(subject);

		subject = new Subject();
		subject.setName("SubjectD");
		subject.setSpecies("Human");
		subject.setGender("U");
		subject.setNotes("This is an example subject");
		subject.addAlias("subject_d");
		subject.addAttribute("tag", "tagB");
		subjects.add(subject);

		subject = new Subject();
		subject.setName("SubjectE");
		subject.setSpecies("Mouse");
		subject.setGender("F");
		subject.setNotes("This is an example subject");
		subject.addAlias("subject_e");
		subject.addAttribute("tag", "tagA");
		subjects.add(subject);
		
		return subjects;
		
	}
	
}
