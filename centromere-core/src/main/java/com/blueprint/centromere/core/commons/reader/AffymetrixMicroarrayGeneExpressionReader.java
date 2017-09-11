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

package com.blueprint.centromere.core.commons.reader;

import com.blueprint.centromere.core.commons.model.AffymetrixArrayData;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.support.DataSetSupport;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.exception.InvalidGeneException;
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordLineFileReader;
import com.blueprint.centromere.core.model.ModelSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public abstract class AffymetrixMicroarrayGeneExpressionReader<T extends AffymetrixArrayData> 
    extends MultiRecordLineFileReader<T> implements SampleAware, ModelSupport<T> {
  
  private static final Logger logger = LoggerFactory.getLogger(AffymetrixMicroarrayGeneExpressionReader.class);
  
  private final DataSetSupport dataSetSupport;
  private final DataImportProperties dataImportProperties;
  private final Class<T> model;
  
  private Map<String, Sample> sampleMap = new HashMap<>();
  private List<String> headers = new ArrayList<>();

  public AffymetrixMicroarrayGeneExpressionReader(
      DataSetSupport dataSetSupport,
      DataImportProperties dataImportProperties, Class<T> model) {
    this.dataSetSupport = dataSetSupport;
    this.dataImportProperties = dataImportProperties;
    this.model = model;
  }

  @Override
  protected List<T> getRecordsFromLine(String line) throws DataImportException {

    String[] bits = line.split(this.getDelimiter());
    List<T> dtoList = new ArrayList<>();

    //Make sure the row has data
    if (bits.length > 2 && !bits[1].trim().equals("")){

      String accession = bits[0]; //  Accession number, e.g. 10000_at
      
      Optional<Gene> geneOptional = getGeneFromLine(line);
      if (!geneOptional.isPresent()){
        if (dataImportProperties.isSkipInvalidGenes()){
          logger.warn(String.format("Unable to extract gene from line: %s", line));
          return new ArrayList<>();
        } else {
          throw new InvalidGeneException(String.format("Unable to extract gene from line: %s", line));
        }
      }
      Gene gene = geneOptional.get();

      for (int i = 2; i < bits.length; i++){

        //Check the header
        if (i < headers.size()) {
          String header = headers.get(i);
          if (!header.equals("empty")
              && !header.toLowerCase().equals("description")
              && !header.toLowerCase().equals("name")) {

            Sample sample = sampleMap.get(headers.get(i));

            try {
              T record = this.getModel().newInstance();
              record.setDataSetId(this.getDataSet().getId());
              record.setDataFileId(this.getDataFile().getId());
              record.setGeneId(gene.getId());
              record.setSampleId(sample.getId());
              record.setSubjectId(sample.getSubjectId());
              record.setProbeSetId(accession);
              record.setValue(Double.parseDouble(bits[i].trim()));
              dtoList.add(record);
            } catch (Exception e){
              throw new DataImportException(e);
            }
            
          }
        } //else throw new DataImportException("The number of data columns exceeds the number of header columns.  Cannot determine sample identifier. Current line: " + line);
      }
    }
    
    return dtoList;
    
  }

  /**
   * Attempts to extract a {@link Gene} record from the line.
   * 
   * @param line
   * @return
   */
  abstract protected Optional<Gene> getGeneFromLine(String line);

  @Override
  protected void parseHeader(String line) throws DataImportException {
    headers = new ArrayList<>();
    sampleMap = new HashMap<>();
    for (String header: line.split(this.getDelimiter())){
      if (header.equals("")){
        header = "empty";
      }
      headers.add(header);
    }
    for (int i = 2; i < headers.size(); i++){
      String header = headers.get(i);
      if (!header.equals("empty")) {
        Optional<Sample> optional = dataSetSupport.findOrCreateSample(header, this.getDataSet());
        if (!optional.isPresent()) {
          if (!dataImportProperties.isSkipInvalidSamples()){
            throw new InvalidSampleException(String.format("Unknown sample: %s", header));
          } else {
            logger.warn(String.format("Cannot identify or create sample: %s", header));
            continue;
          }
        }
        sampleMap.put(header, optional.get());
      }
    }
  }

  @Override
  public List<Sample> getSamples() {
    return new ArrayList<>(sampleMap.values());
  }

  @Override
  protected boolean isSkippableLine(String line) {
    return false;
  }

  @Override
  public Class<T> getModel() {
    return model;
  }

  @Override
  public void setModel(Class<T> model) {

  }
}
