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

package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.config.WebProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author woemler
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
@Profile({ Profiles.WEB_PROFILE })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserDetailsService userService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Configuration
    @Order(1)
    @Profile({ Profiles.NO_SECURITY })
    public static class OpenReadWriteTokenSecurityConfig extends TokenSecurityConfiguration {
        
        @Autowired private WebProperties webProperties;
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {

            String secureUrl = webProperties.getSecurity().getSecureUrl();
            logger.info(String.format("Configuring web security with OPEN READ and OPEN WRITE for " 
                + "API root %s", secureUrl));

            http
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(authenticationTokenProcessingFilter(),
                    UsernamePasswordAuthenticationFilter.class)
                    .antMatcher(secureUrl)
                    .authorizeRequests()
                        .anyRequest().permitAll()
//                        .antMatchers(HttpMethod.GET, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.POST, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.PUT, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.DELETE, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.PATCH, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.OPTIONS, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.HEAD, secureUrl).permitAll()
                .and().csrf()
                    .disable();

        }
    }

//    @Configuration
//    @Order(1)
//    @Profile({Profiles.SECURE_WRITE_PROFILE})
//    public static class SecuredWriteTokenSecurityConfig extends TokenSecurityConfiguration {
//
//        @Autowired private Environment env;
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//
//            String secureUrl = env.getRequiredProperty("centromere.security.secure-url");
//            logger.info(String.format("Configuring web security with OPEN READ and RESTRICTED WRITE "
//                + "for API root %s", secureUrl));
//
//            http
//                .sessionManagement()
//                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and().addFilterBefore(authenticationTokenProcessingFilter(),
//                    UsernamePasswordAuthenticationFilter.class)
//                    .antMatcher(secureUrl)
//                    .authorizeRequests()
//                        .antMatchers(HttpMethod.GET, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.POST, secureUrl).fullyAuthenticated()
//                        .antMatchers(HttpMethod.PUT, secureUrl).fullyAuthenticated()
//                        .antMatchers(HttpMethod.DELETE, secureUrl).fullyAuthenticated()
//                        .antMatchers(HttpMethod.PATCH, secureUrl).fullyAuthenticated()
//                        .antMatchers(HttpMethod.OPTIONS, secureUrl).permitAll()
//                        .antMatchers(HttpMethod.HEAD, secureUrl).permitAll()
//                .and().csrf()
//                    .disable();
//
//        }
//    }

    @Configuration
    @Order(1)
    @Profile({Profiles.SECURE_READ_WRITE_PROFILE})
    public static class SecuredReadWriteTokenSecurityConfig extends TokenSecurityConfiguration {

        @Autowired private WebProperties webProperties;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            String secureUrl = webProperties.getSecurity().getSecureUrl();
            logger.info(String.format("Configuring web security with RESTRICTED READ and RESTRICTED " 
                + "WRITE for API root: %s", secureUrl));

            http
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(authenticationTokenProcessingFilter(),
                    UsernamePasswordAuthenticationFilter.class)
                    .antMatcher(secureUrl)
                    .authorizeRequests()
                        .antMatchers(HttpMethod.GET, secureUrl).fullyAuthenticated()
                        .antMatchers(HttpMethod.POST, secureUrl).fullyAuthenticated()
                        .antMatchers(HttpMethod.PUT, secureUrl).fullyAuthenticated()
                        .antMatchers(HttpMethod.DELETE, secureUrl).fullyAuthenticated()
                        .antMatchers(HttpMethod.PATCH, secureUrl).fullyAuthenticated()
                        .antMatchers(HttpMethod.OPTIONS, secureUrl).fullyAuthenticated()
                        .antMatchers(HttpMethod.HEAD, secureUrl).fullyAuthenticated()
                .and().csrf()
                    .disable();

        }
    }

    @Configuration
    @Order(2)
    public static class BasicWebSecurtiyConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                    .anyRequest().permitAll()
                .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().httpBasic()
                .and().csrf()
                    .disable();
        }
    }

}
