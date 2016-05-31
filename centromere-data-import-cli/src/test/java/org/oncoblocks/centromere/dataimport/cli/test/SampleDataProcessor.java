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

package org.oncoblocks.centromere.dataimport.cli.test;

import org.oncoblocks.centromere.core.dataimport.*;
import org.oncoblocks.centromere.dataimport.cli.test.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
@Component
@DataTypes({"sample_data"})
public class SampleDataProcessor extends GenericRecordProcessor<SampleData> {
	
	private final DataFileRepository dataFileRepository;
	private final DataSetRepository dataSetRepository;

	@Autowired
	public SampleDataProcessor(SampleDataRepository repository, DataFileRepository dataFileRepository,
			DataSetRepository dataSetRepository) {
		super(SampleData.class, new SampleDataReader(), new SampleDataValidator(), 
				new RepositoryRecordWriter<>(repository));
		this.dataFileRepository = dataFileRepository;
		this.dataSetRepository = dataSetRepository;
	}

	@Override 
	public void doBefore(Object... args) throws DataImportException {
		Assert.isTrue(args[0] instanceof HashMap);
		Map<String,String> params = (Map<String, String>) args[0];
		DataSet dataSet = this.getDataSet(params);
		DataFile dataFile = this.getDataFile(params, dataSet);
		((SampleDataReader) this.getReader()).setDataFile(dataFile);
		super.doBefore(args);
	}
	
	private DataSet getDataSet(Map<String,String> params){
		DataSet dataSet = null;
		List<DataSet> dataSets = dataSetRepository.findByLabel(params.get("dataSetLabel"));
		if (dataSets == null || dataSets.isEmpty()){
			dataSet = new DataSet();
			dataSet.setLabel(params.get("dataSetLabel"));
			dataSet.setSource(params.get("dataSetSource"));
			dataSet.setName(params.get("dataSetName"));
			dataSet = dataSetRepository.insert(dataSet);
		} else {
			dataSet = dataSets.get(0);
		}
		return dataSet;
	}
	
	private DataFile getDataFile(Map<String, String> params, DataSet dataSet){
		DataFile dataFile = null;
		List<DataFile> dataFiles = dataFileRepository.findByFilePath(params.get("dataFilePath"));
		if (dataFiles == null || dataFiles.isEmpty()){
			dataFile = new DataFile();
			dataFile.setFilePath(params.get("dataFilePath"));
			dataFile.setDataType("sample_data");
			dataFile.setDataSetId(dataSet.getId());
			dataFile = dataFileRepository.insert(dataFile);
		} else {
			dataFile = dataFiles.get(0);
		}
		return dataFile;
	}

	// Reader
	public static class SampleDataReader extends RecordCollectionReader<SampleData>  {
		
		private DataFile dataFile;
		
		public SampleDataReader() {
			super(SampleData.createSampleData());
		}

		@Override 
		public SampleData readRecord() throws DataImportException {
			SampleData data = super.readRecord();
			if (data != null){
				data.setDataFileId(dataFile.getId());
			}
			return data;
		}

		public void setDataFile(DataFile dataFile) {
			this.dataFile = dataFile;
		}
	}
	
	// Validator
	public static class SampleDataValidator implements Validator {

		public boolean supports(Class<?> aClass) {
			return SampleData.class.equals(aClass);
		}

		public void validate(Object o, Errors errors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.empty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dataFileId", "dataFileId.empty");
		}
	}
	
}
