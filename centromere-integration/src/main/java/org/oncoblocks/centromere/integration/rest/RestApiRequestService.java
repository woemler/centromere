package org.oncoblocks.centromere.integration.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author woemler
 * @since 0.4.2
 */
public class RestApiRequestService {

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Fetches a single object, given a URL, mapping class, and an array of parameters.
	 * 
	 * @param url
	 * @param type
	 * @param parameters
	 * @return
	 */
	public Object fetchSingle(String url, Class<?> type, Object[] parameters){
		HttpEntity<String> request = createRequest();
		ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				String.class,
				parameters.length == 1 ? parameters[0] : parameters
		);
		try {
			if (RestApiRequestService.isError(response.getStatusCode())) {
				RestError restError = objectMapper.readValue(response.getBody(), RestError.class);
				throw new RestClientException("[" + restError.getCode() + "] " + restError.getMessage());
			} else {
				return objectMapper.readValue(response.getBody(), type);
			}
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public Map<String,Object> fetchSingle(String url, Object[] parameters){
		HttpEntity<String> request = createRequest();
		ResponseEntity<Map<String,Object>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<Map<String,Object>>(){},
				parameters.length == 1 ? parameters[0] : parameters
		);
		return response.getBody();
	}

	/**
	 * Fetches multiple objects from a REST web service, given a URL, mappable class, and an array 
	 *   of parameters.
	 * 
	 * @param url
	 * @param type
	 * @param parameters
	 * @return
	 */
	public List<Object> fetchMultiple(String url, Class<?> type, Object[] parameters){
		HttpEntity<String> request = createRequest();
		ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				String.class,
				parameters
		);
		try {
			if (RestApiRequestService.isError(response.getStatusCode())) {
				RestError restError = objectMapper.readValue(response.getBody(), RestError.class);
				throw new RestClientException("[" + restError.getCode() + "] " + restError.getMessage());
			} else {
				return objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory()
						.constructCollectionType(List.class, type));

			}
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public List<Map<String,Object>> fetchMultiple(String url, Object[] parameters){
		HttpEntity<String> request = createRequest();
		ResponseEntity<List<Map<String,Object>>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<List<Map<String,Object>>>(){},
				parameters.length == 1 ? parameters[0] : parameters
		);
		return response.getBody();
	}

	/**
	 * Fetches multiple objects from a REST web service, given a URL and mappable class. 
	 * 
	 * @param url
	 * @param type
	 * @return
	 */
	private List<Object> fetchAll(String url, Class<?> type){
		return fetchMultiple(url, type, new Object[]{});
	}

	/**
	 * Fetches multiple objects from a REST web service, given a URL and mappable class. 
	 *
	 * @param url
	 * @return
	 */
	private List<Map<String,Object>> fetchAll(String url){
		return fetchMultiple(url, new Object[]{});
	}

	/**
	 * Creates an {@link HttpEntity} request object with the default JSON media type.
	 * 
	 * @return
	 */
	public static HttpEntity<String> createRequest(){
		return createRequest(MediaType.APPLICATION_JSON_VALUE, new HashMap<>());
	}

	/**
	 * Creates an {@link HttpEntity} request object, given a media type.
	 * 
	 * @param mediaType
	 * @return
	 */
	public static HttpEntity<String> createRequest(String mediaType){
		return createRequest(mediaType, new HashMap<>());
	}

	/**
	 * Creates an {@link HttpEntity} request object, given a request media type and optional headers.
	 * 
	 * @param mediaType
	 * @param headers
	 * @return
	 */
	public static HttpEntity<String> createRequest(String mediaType, Map<String, String> headers){
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Accept", mediaType);
		if (headers != null && !headers.isEmpty()){
			for (Map.Entry entry: headers.entrySet()){
				httpHeaders.add((String) entry.getKey(), (String) entry.getValue());
			}
		}
		return new HttpEntity<>(httpHeaders);
	}

	/**
	 * Tests to see if the given HTTP response code represents an error.
	 * 
	 * @param status
	 * @return
	 */
	public static boolean isError(HttpStatus status) {
		HttpStatus.Series series = status.series();
		return !status.equals(HttpStatus.NOT_FOUND) && (HttpStatus.Series.SERVER_ERROR.equals(series) 
				|| HttpStatus.Series.CLIENT_ERROR.equals(series));
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
