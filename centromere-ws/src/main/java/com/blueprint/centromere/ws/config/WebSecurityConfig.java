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

import com.blueprint.centromere.ws.security.RestAuthenticationEntryPoint;
import com.blueprint.centromere.ws.security.TokenOperations;
import com.blueprint.centromere.ws.security.simple.AuthenticationTokenProcessingFilter;
import com.blueprint.centromere.ws.security.simple.SimpleTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Default web security configurations.
 *
 * @author woemler
 */
public final class WebSecurityConfig  {

    public static final String NO_SECURITY_PROFILE = "web_security_none";
    public static final String SIMPLE_TOKEN_SECURITY_PROFILE = "web_security_simple_token";

    private WebSecurityConfig() {
    }

    @Configuration
    @EnableWebSecurity
    //@EnableGlobalMethodSecurity
    @PropertySource(value = { "classpath:web-defaults.properties" })
    @Profile(SIMPLE_TOKEN_SECURITY_PROFILE )
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public static class DefaultWebSecurityConfig extends WebSecurityConfigurerAdapter {

        private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

        @Autowired
        private Environment env;

        @SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
        @Autowired
        private UserDetailsService userService;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationEntryPoint unauthorizedHandler() {
            return new RestAuthenticationEntryPoint();
        }

        @Bean
        public TokenOperations tokenOperations() {
            SimpleTokenProvider tokenUtils = new SimpleTokenProvider(env.getRequiredProperty("centromere.web.security.token"));
            tokenUtils.setTokenLifespan(getTokenLifespan());
            return tokenUtils;
        }

        @Bean
        public AuthenticationTokenProcessingFilter authenticationTokenProcessingFilter() {
            return new AuthenticationTokenProcessingFilter(tokenOperations(), userService);
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            String secureUrl = env.getRequiredProperty("centromere.web.security.secure-url");
            LOGGER.info(String.format("Configuring web security with RESTRICTED READ and RESTRICTED "
                + "WRITE with simple token authentication for API root: %s", secureUrl));

            http
                .cors()
                .and()
                .csrf()
                .disable()
                .httpBasic()
                .and()
                .formLogin()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/authenticate")
                .permitAll()
                .anyRequest()
                .fullyAuthenticated()
            ;

            http
                .addFilterBefore(authenticationTokenProcessingFilter(),
                    UsernamePasswordAuthenticationFilter.class);

        }

        protected Long getTokenLifespan() {
            long hour = 1000L * 60 * 60;
            Long lifespan = hour * 24; // one day
            if (!env.getProperty("centromere.web.security.token-lifespan-hours").equals("")) {
                lifespan = hour * env.getRequiredProperty("centromere.web.security.token-lifespan-hours", Long.class);
            } else if (!env.getProperty("centromere.web.security.token-lifespan-days").equals("")) {
                lifespan = hour * 24 * env.getRequiredProperty("centromere.web.security.token-lifespan-days", Long.class);
            }
            return lifespan;
        }

    }

}
