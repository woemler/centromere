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

package org.oncoblocks.centromere.dataimport.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.blueprint.centromere.core.config.ModelRegistry;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.RepositoryOperations;
import com.blueprint.centromere.core.util.JsonModelConverter;
import com.blueprint.centromere.core.util.KeyValueMapModelConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

/**
 * Executes the {@code add} command, based upon input arguments.  The {@code category} argument will
 *   determine how the {@code label} and {@code body} objects are processed.
 * 
 * @author woemler
 */
public class AddCommandRunner {
	
	private ModelRegistry modelRegistry;
	private ObjectMapper objectMapper = new ObjectMapper();
	private Validator validator;
	
	private static final Logger logger = LoggerFactory.getLogger(AddCommandRunner.class);

	/**
	 * Takes parsed {@link AddCommandArguments} and delegates the submitted arguments based upon the 
	 *   {@code category} argument.  The {@code label} and {@code body} are passed to methods that 
	 *   handle their specific content and use-cases.
	 * 
	 * @param arguments
	 * @throws Exception
	 */
	public <S extends Model<?>> void run(AddCommandArguments arguments) throws Exception {
		logger.debug(String.format("[CENTROMERE] Running AddCommandRunner with arguments: %s", arguments.toString()));
		Assert.notNull(arguments, "AddCommandArguments must not be null!");
		Class<S> model = (Class<S>) getModelFromTypeName(arguments.getType());
		if (model == null){
			throw new DataImportException(String.format("Unable to identify model type: %s", arguments.getType()));
		}
		S entity = (S) convertInputToModel(arguments, model);
		if (entity == null){
			throw new DataImportException(String.format("Unable to convert input to %s model object.", model.getName()));
		}
		if (validator != null && validator.supports(model)){
			BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(entity, entity.getClass().getName());
			validator.validate(entity, bindingResult);
			if (bindingResult.hasErrors()){
				throw new DataImportException(String.format("Model object is invalid and has errors: %s", bindingResult.getAllErrors().toString()));
			}
		}
		RepositoryOperations<S, ?> repository = (RepositoryOperations<S, ?>) getModelRepository(model);
		if (repository == null){
			throw new DataImportException("No repository available for model class " + model.getName());
		}
		repository.insert(entity);
		logger.debug("[CENTROMERE] Add task complete.");
	}
	
	private RepositoryOperations<?, ?> getModelRepository(Class<? extends Model> model) throws DataImportException {
		RepositoryOperations repository = null;
		if (modelRegistry.isSupported(model)){
			repository = modelRegistry.getModelRepository(model);
			if (repository == null){
				throw new DataImportException(String.format("No registered repository found for model " 
						+ "class %s.", model.getName()));
			}
		} 
		return repository;
	}

	/**
	 * Checks to see if the submitted model reference refers to a registered model class or is a
	 *   direct reference to a Java class.
	 * 
	 * @param name
	 * @return
	 */
	private Class<? extends Model> getModelFromTypeName(String name){
		Class<? extends Model> model = null;
		if (modelRegistry.isSupported(name)){
			model = modelRegistry.getModel(name);
		} else {
			logger.warn(String.format("Input type %s is not a valid class.", name));
		}
		return model;
	}

	/**
	 * Attempts to convert the input arguments into a model object.
	 * 
	 * @param arguments
	 * @param model
	 * @return
	 */
	private Object convertInputToModel(AddCommandArguments arguments, Class<? extends Model> model){
		Object entity = null;
		Converter converter = null;
		if (arguments.getBody() != null){
			converter = new JsonModelConverter(objectMapper, model);
			entity = converter.convert(arguments.getBody());
		} else if (arguments.getParameters() != null){
			converter = new KeyValueMapModelConverter(objectMapper, model);
			entity = converter.convert(arguments.getParameters());
		}
		return entity;
	}

	@Autowired
	public void setModelRegistry(ModelRegistry modelRegistry) {
		this.modelRegistry = modelRegistry;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
