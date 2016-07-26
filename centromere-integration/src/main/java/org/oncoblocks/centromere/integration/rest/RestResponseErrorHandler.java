package org.oncoblocks.centromere.integration.rest;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * @author woemler
 * @since 0.4.2
 */
public class RestResponseErrorHandler implements ResponseErrorHandler {

	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return RestApiRequestService.isError(response.getStatusCode());
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		System.out.println(String.format("Response error : %s %s", response.getStatusCode().toString(), response.getStatusText()));
	}

}
