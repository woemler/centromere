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

package com.blueprint.centromere.cli.manifest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Data;

/**
 * @author woemler
 * @since 0.5.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ImportManifest {
    
  private String name;
  private String dataSetId;
  private String source;
  private String version;
  private String description;
  private LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
  private LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
  private List<ManifestFile> files = new ArrayList<>();

}
