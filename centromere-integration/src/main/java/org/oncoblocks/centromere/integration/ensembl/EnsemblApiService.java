package org.oncoblocks.centromere.integration.ensembl;

import org.oncoblocks.centromere.integration.rest.RestApiRequestService;

import java.util.Map;

/**
 * @author woemler
 * @since 0.4.2
 */
public class EnsemblApiService {
	
	private final RestApiRequestService requestService;

	public EnsemblApiService(RestApiRequestService requestService) {
		this.requestService = requestService;
	}

	public boolean pingServer(){
		Map<String,Object> response = requestService.fetchSingle(EnsemblApiUrls.PING_URL, new String[]{});
		return response.containsKey("ping");
	}
	
}
