package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.tests.core.models.Gene;

/**
 * @author woemler
 */
public class CSVGeneInfoReader<T extends Gene<?>> extends GeneInfoReader<T> {

    public CSVGeneInfoReader(Class<T> model) {
        super(model, ",");
    }

}
