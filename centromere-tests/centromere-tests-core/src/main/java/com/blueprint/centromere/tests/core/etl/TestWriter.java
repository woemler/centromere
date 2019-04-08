package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.core.etl.writer.RecordWriter;
import com.blueprint.centromere.core.exceptions.DataProcessingException;
import com.blueprint.centromere.core.model.Model;
import java.io.File;
import java.util.Map;

/**
 * @author woemler
 */
public class TestWriter<T extends Model<?>> implements RecordWriter<T> {

    @Override
    public void writeRecord(T record) throws DataProcessingException {
        System.out.println("Running writeRecord method");
    }

    @Override
    public void doBefore(File file, Map<String, String> args) throws DataProcessingException {
        System.out.println("Running doBefore method");
    }

    @Override
    public void doOnSuccess(File file, Map<String, String> args) throws DataProcessingException {
        System.out.println("Running doOnSuccess method");
    }

    @Override
    public void doOnFailure(File file, Map<String, String> args) throws DataProcessingException {
        System.out.println("Running doOnFailure method");
    }
}
