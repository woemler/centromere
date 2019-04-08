package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.core.etl.processor.DataTypes;
import com.blueprint.centromere.core.etl.processor.GenericDataImportProcessor;
import com.blueprint.centromere.tests.core.models.Gene;
import java.io.Serializable;
import org.springframework.validation.Validator;

/**
 * @author woemler
 */
@DataTypes({"gene_info"})
public class GeneInfoProcessor<T extends Gene<I>, I extends Serializable> extends GenericDataImportProcessor<T> {

    public GeneInfoProcessor(Class<T> model,
        GeneInfoReader<T> reader,
        TestWriter<T> writer) {
        super(model, reader, writer);
    }

    public GeneInfoProcessor(Class<T> model,
        GeneInfoReader<T> reader,
        TestWriter<T> writer,
        Validator validator) {
        super(model, reader, writer, validator);
    }

}
