package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.core.etl.DataImportException;
import com.blueprint.centromere.core.etl.reader.StandardRecordFileReader;
import com.blueprint.centromere.tests.core.models.Gene;

/**
 * @author woemler
 */
public class GeneInfoReader<T extends Gene<?>> extends StandardRecordFileReader<T> {

  public GeneInfoReader(Class<T> model) {
    super(model);
  }

  protected T getRecordFromLine(String line) throws DataImportException {
    
    String[] bits = line.split("\\t");
    T gene;
    
    try {
      gene = this.getModel().newInstance();
    } catch (Exception e){
      throw new DataImportException(e);
    }
    
    gene.setTaxId(Integer.parseInt(bits[0]));
    gene.setEntrezGeneId(Integer.parseInt(bits[1]));
    gene.setSymbol(bits[2]);
    for (String alias: bits[4].split("\\|")){
      if (!alias.replaceAll("-", "").equals("")) gene.addAlias(alias);
    }
    for (String ref : bits[5].split("\\|")) {
      String[] r = ref.split(":");
      if (!r[0].replaceAll("-", "").equals("")) gene.addExternalReference(r[0], r[r.length - 1]);
    }
    gene.setChromosome(bits[6]);
    gene.setChromosomeLocation(bits[7]);
    gene.setDescription(bits[8]);
    gene.setGeneType(bits[9]);
    
    return gene;
    
  }

  @Override
  protected boolean isSkippableLine(String line) {
    return line.trim().equals("") || line.startsWith("#");
  }

  @Override
  protected boolean isHeaderLine(String line) {
    return false;
  }

}
