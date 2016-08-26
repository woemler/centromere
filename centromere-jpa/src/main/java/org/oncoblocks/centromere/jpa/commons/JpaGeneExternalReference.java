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

package org.oncoblocks.centromere.jpa.commons;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author woemler
 * @since 0.4.3
 */
@Entity
@Table(name = "entrez_gene_database_references")
public class JpaGeneExternalReference implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "database_reference_id", nullable = false, updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gene_id")
	private JpaGene gene;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "value", nullable = false, length = 1024)
	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JpaGene getGene() {
		return gene;
	}

	public void setGene(JpaGene gene) {
		this.gene = gene;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return "JpaEntrezGeneDatabaseCrossReference{" +
				"id=" + id +
				", name='" + name + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
