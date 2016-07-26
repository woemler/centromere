package org.oncoblocks.centromere.integration.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

/**
 * @author woemler
 * @since 0.4.2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestError {

	private HttpStatus status;
	private Integer code;
	private String message;
	private String developerMessage;
	private String moreInfoUrl;

	public RestError() { }

	public RestError(HttpStatus status, Integer code, String message, String developerMessage,
			String moreInfoUrl) {
		if (status == null){
			throw new NullPointerException("HttpStatus argument cannot be null.");
		}
		this.status = status;
		this.code = code;
		this.message = message;
		this.developerMessage = developerMessage;
		this.moreInfoUrl = moreInfoUrl;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public String getMoreInfoUrl() {
		return moreInfoUrl;
	}

}
