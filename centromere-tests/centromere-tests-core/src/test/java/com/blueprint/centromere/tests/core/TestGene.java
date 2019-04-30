package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.tests.core.models.Gene;

/**
 * @author woemler
 */
public class TestGene extends Gene<String> {

    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
