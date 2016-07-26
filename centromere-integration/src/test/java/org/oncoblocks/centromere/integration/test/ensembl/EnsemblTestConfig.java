package org.oncoblocks.centromere.integration.test.ensembl;

import org.oncoblocks.centromere.integration.ensembl.EnsemblApiService;
import org.oncoblocks.centromere.integration.rest.RestApiRequestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author woemler
 */
@Configuration
@ComponentScan(basePackages = "org.oncoblocks.centromere.integration.test.ensembl")
public class EnsemblTestConfig {
	
	@Bean
	public RestApiRequestService restApiRequestService(){
		return new RestApiRequestService();
	}
	
	@Bean
	public EnsemblApiService ensemblApiService(){
		return new EnsemblApiService(restApiRequestService());
	}
	
}
