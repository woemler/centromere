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

package com.blueprint.centromere.ws.controller;

import com.blueprint.centromere.ws.config.WebSecurityConfig;
import com.blueprint.centromere.ws.exception.ResourceNotFoundException;
import com.blueprint.centromere.ws.security.TokenDetails;
import com.blueprint.centromere.ws.security.TokenOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication and token distribution in simple-token-security
 * configured web services.
 *
 * @author woemler
 */
@Profile({WebSecurityConfig.SIMPLE_TOKEN_SECURITY_PROFILE})
@RestController
public class UserAuthenticationController {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(UserAuthenticationController.class);

    @Autowired(required = false)
    private TokenOperations tokenOperations;

    /**
     * Takes the {@link UserDetails} that are produced after authentication and generates a security
     * token, based on the {@link TokenDetails} format.  This token will allow the user to make
     * requests to the main API endpoints.
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public TokenDetails createToken(@AuthenticationPrincipal UserDetails user) {
        if (tokenOperations == null) {
            throw new ResourceNotFoundException();
        }
        Assert.notNull(user, "Unable to authenticate user!");
        TokenDetails tokenDetails = tokenOperations.createTokenAndDetails(user);
        LOGGER.info(String.format("Successfully generated authentication token for user: %s",
            user.getUsername()));
        return tokenDetails;
    }

}
