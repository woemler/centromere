/*
 * Copyright 2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueprint.centromere.core.ws.config;

import com.blueprint.centromere.core.ws.security.AuthenticationTokenProcessingFilter;
import com.blueprint.centromere.core.ws.security.BasicTokenUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author woemler
 */
public abstract class TokenSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenSecurityConfiguration.class);

	@SuppressWarnings("SpringJavaAutowiringInspection") 
	@Autowired
	private UserDetailsService userService;

	@Autowired
	private Environment env;

	@Bean
	public BasicTokenUtils tokenUtils() {
		BasicTokenUtils tokenUtils = new BasicTokenUtils(env.getRequiredProperty("centromere.security.token"));
		tokenUtils.setTokenLifespan(getTokenLifespan());
		return tokenUtils;
	}

	@Bean
	public AuthenticationTokenProcessingFilter authenticationTokenProcessingFilter() {
		return new AuthenticationTokenProcessingFilter(tokenUtils(), userService);
	}
	
	protected Long getTokenLifespan(){
		long hour = 1000L * 60 * 60;
		Long lifespan = hour * 24; // one day
		try {
			lifespan = hour * Long.parseLong(env.getRequiredProperty("centromere.security.token-lifespan-hours"));
		} catch (NumberFormatException e){
			try {
				lifespan = hour * 24 * Long.parseLong(env.getRequiredProperty("centromere.security.token-lifespan-days"));
			} catch (NumberFormatException ex){
				logger.warn("[CENTROMERE] Token lifespan not properly configured.  Reverting to default configuration");
			}
		}
		return lifespan;
	}
	
}
