package org.oncoblocks.centromere.commons.repository;

import org.oncoblocks.centromere.commons.model.EntrezGene;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;

import java.io.Serializable;
import java.util.List;

/**
 * @author woemler
 */
public interface EntrezGeneRepository<T extends EntrezGene<ID>, ID extends Serializable> 
		extends RepositoryOperations<T, ID> {
	List<T> findByEntrezGeneId(long entrezGeneId);
	List<T> findByPrimaryGeneSymbol(String primaryGeneSymbol);
}
