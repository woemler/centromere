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

package com.blueprint.centromere.core.commons.processors;

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.DataSet;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.repositories.DataFileRepository;
import com.blueprint.centromere.core.commons.repositories.DataSetRepository;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.DataSetAware;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.GenericRecordProcessor;
import com.blueprint.centromere.core.model.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author woemler
 * @since 0.5.0
 */
public abstract class CommonsDataProcessor<T extends Model<?>> extends GenericRecordProcessor<T> {

    private static final Logger logger = LoggerFactory.getLogger(CommonsDataProcessor.class);

    @Autowired private Environment environment;
    @Autowired private DataSetRepository dataSetRepository;
    @Autowired private DataFileRepository dataFileRepository;

    private Boolean isSkippable = false;
    private DataSet dataSet;
    private DataFile dataFile;

    @Override
    public void doBefore(Object... args) throws DataImportException {

        super.doBefore(args);

        String filePath;
        String dataType;
        isSkippable = false;
        dataSet = null;
        dataFile = null;

        try {
            Assert.isTrue(args.length > 1, "Must be at least one argument submitted to processor.  Received none.");
            Assert.isTrue(args[0] instanceof String, "First processor argument must be a file path string");
            Assert.isTrue(args[1] instanceof String, "Second processor argument must be a data type string");

            filePath = (String) args[0];
            dataType = (String) args[1];
            File file = new File(filePath);
            Assert.isTrue(file.isFile() && file.canRead(), String.format("Input file is not readable: %s", filePath));

            Assert.isTrue(!environment.containsProperty("dataSet.name"), "DataSet name must be set as environment property for data file processing.");
        } catch (IllegalArgumentException e){
            throw new DataImportException(e.getMessage());
        }

        String dataSetName = environment.getRequiredProperty("dataSet.name");
        dataSet = dataSetRepository.findOneByName(dataSetName);
        if (dataSet == null){
            dataSet = new DataSet();
            dataSet.setName(dataSetName);
            if (environment.containsProperty("dataSet.source")){
                dataSet.setSource(environment.getRequiredProperty("dataSet.source"));
            }
            if (environment.containsProperty("dataSet.version")){
                dataSet.setVersion(environment.getRequiredProperty("dataSet.version"));
            }
            if (environment.containsProperty("dataSet.description")){
                dataSet.setDescription(environment.getRequiredProperty("dataSet.description"));
            }
            dataSetRepository.save(dataSet);
        }

        dataFile = dataFileRepository.findOneByFilePath(filePath);
        if (dataFile == null){
            dataFile = new DataFile();
            dataFile.setDataSet(dataSet);
            dataFile.setDataType(dataType);
            dataFile.setFilePath(filePath);
            dataFile.setDateCreated(new Date());
            dataFile.setDateUpdated(new Date());
        } else if (environment.containsProperty("centromere.import.skip-existing-files")
                && environment.getRequiredProperty("centromere.import.skip-existing-files", Boolean.class)){
            isSkippable = true;
            logger.info(String.format("DataFile record for the current file exists, and will not be overwritten: %s", filePath));
        }
        dataFileRepository.save(dataFile);

        if (this.getReader() instanceof DataFileAware) {
            ((DataFileAware) this.getReader()).setDataFile(dataFile);
        }
        if (this.getWriter() instanceof DataFileAware) {
            ((DataFileAware) this.getWriter()).setDataFile(dataFile);
        }
        if (this.getImporter() instanceof DataFileAware) {
            ((DataFileAware) this.getImporter()).setDataFile(dataFile);
        }
        if (this.getReader() instanceof DataSetAware) {
            ((DataSetAware) this.getReader()).setDataSet(dataSet);
        }
        if (this.getWriter() instanceof DataSetAware) {
            ((DataSetAware) this.getWriter()).setDataSet(dataSet);
        }
        if (this.getImporter() instanceof DataSetAware) {
            ((DataSetAware) this.getImporter()).setDataSet(dataSet);
        }

    }

    @Override
    public void run(Object... args) throws DataImportException {
        if (isSkippable){
            logger.info("Current file marked as skippable.  Skipping run operation.");
            return;
        }
        super.run(args);
    }

    /**
     * Updates the {@link DataSet} record with newly parsed samples.
     *
     * @param args an array of objects of any type.
     * @throws DataImportException
     */
    @Override
    public void doAfter(Object... args) throws DataImportException {
        if (this.getReader() instanceof SampleAware){
            List<Sample> samples = ((SampleAware) this.getReader()).getSamples();
            samples.addAll(dataSet.getSamples());
            dataSet.setSamples(samples);
            dataSetRepository.save(dataSet);
        }
    }

    protected Boolean getSkippable() {
        return isSkippable;
    }

    protected void setSkippable(Boolean skippable) {
        isSkippable = skippable;
    }

    protected DataSet getDataSet() {
        return dataSet;
    }

    protected void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    protected DataFile getDataFile() {
        return dataFile;
    }

    protected void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }
}
