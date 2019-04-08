package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.core.etl.reader.StandardFileRecordReader;
import com.blueprint.centromere.core.exceptions.DataProcessingException;
import com.blueprint.centromere.tests.core.models.Gene;
import com.google.common.base.Joiner;
import java.util.List;

/**
 * @author woemler
 */
public class GeneInfoReader<T extends Gene<?>> extends StandardFileRecordReader<T> {

    public GeneInfoReader(Class<T> model, String delimiter) {
        super(model, delimiter);
    }

    public GeneInfoReader(Class<T> model) {
        super(model);
    }

    protected T getRecordFromLine(List<String> line) throws DataProcessingException {

        T gene;

        try {
            gene = this.getModel().newInstance();
        } catch (Exception e) {
            throw new DataProcessingException(e);
        }

        gene.setTaxId(Integer.parseInt(line.get(0)));
        gene.setEntrezGeneId(Integer.parseInt(line.get(1)));
        gene.setSymbol(line.get(2));
        for (String alias: line.get(4).split("\\|")) {
            if (!alias.replaceAll("-", "").equals("")) {
                gene.addAlias(alias);
            }
        }
        for (String ref : line.get(5).split("\\|")) {
            String[] r = ref.split(":");
            if (!r[0].replaceAll("-", "").equals("")) {
                gene.addExternalReference(r[0], r[r.length - 1]);
            }
        }
        gene.setChromosome(line.get(6));
        gene.setChromosomeLocation(line.get(7));
        gene.setDescription(line.get(8));
        gene.setGeneType(line.get(9));

        return gene;

    }

    @Override
    protected boolean isSkippableLine(List<String> line) {
        return Joiner.on("").join(line).trim().equals("") || line.get(0).startsWith("#");
    }

    @Override
    protected boolean isHeaderLine(List<String> line) {
        return false;
    }

}
