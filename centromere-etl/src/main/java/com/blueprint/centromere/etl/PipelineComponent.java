package com.blueprint.centromere.etl;

import java.io.File;
import java.util.Map;

/**
 * @author woemler
 */
public interface PipelineComponent {

  void doBefore(File file, Map<String, String> args) throws DataImportException;

  void doOnSuccess(File file, Map<String, String> args) throws DataImportException;

  void doOnFailure(File file, Map<String, String> args) throws DataImportException;

}
