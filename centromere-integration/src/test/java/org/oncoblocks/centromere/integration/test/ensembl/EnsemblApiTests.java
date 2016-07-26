package org.oncoblocks.centromere.integration.test.ensembl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.integration.ensembl.EnsemblApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EnsemblTestConfig.class })
public class EnsemblApiTests {
	
	@Autowired private EnsemblApiService ensemblApiService;
	
	@Test
	public void pingApiTest() throws Exception {
		Assert.isTrue(ensemblApiService.pingServer());
	}
	
}
