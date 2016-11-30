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

package com.blueprint.centromere.core.ws.controller;

import com.blueprint.centromere.core.commons.models.User;
import com.blueprint.centromere.core.commons.repositories.UserRepository;
import com.blueprint.centromere.core.ws.security.BasicTokenUtils;
import com.blueprint.centromere.core.ws.security.TokenDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author woemler
 */
public class SecurityControllers {

	@Controller
	public static class UserAuthenticationController {

		@Autowired private BasicTokenUtils tokenUtils;
		@Autowired private UserRepository userRepository;
		private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationController.class);

		@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
		public ResponseEntity<TokenDetails> createToken(@AuthenticationPrincipal User user){
			Assert.notNull(user, "Unable to authenticate user!");
			TokenDetails tokenDetails = tokenUtils.createTokenAndDetails(user);
			logger.info(String.format("Successfully generated authentication token for user: %s", user.getUsername()));
			return new ResponseEntity<>(tokenDetails, HttpStatus.OK);
		}

	}
	
}
