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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author woemler
 */
public class WebSecurityConfig  {
  
  public static final String NO_SECURITY_PROFILE = "web_security_none";
  public static final String SECURE_READ_WRITE_PROFILE = "web_security_read_write";

  @Configuration
  @EnableWebSecurity
  @EnableGlobalMethodSecurity
  public static class DefaultWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private UserDetailsService userService;
    
    @Bean
    public PasswordEncoder passwordEncoder(){
      return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
      DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
      authenticationProvider.setPasswordEncoder(passwordEncoder());
      authenticationProvider.setUserDetailsService(userService);
      return authenticationProvider;
    }
    
    

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(authenticationProvider());
//          .userDetailsService(userService)
//          .passwordEncoder(passwordEncoder());
    }

    @Configuration
    @PropertySource(value = { "classpath:web-defaults.properties" })
    @Order(1)
    @Profile(NO_SECURITY_PROFILE)
    public static class OpenReadWriteTokenSecurityConfig extends TokenSecurityConfiguration {

      @Autowired private Environment env;

      @Override
      protected void configure(HttpSecurity http) throws Exception {

        String secureUrl = env.getRequiredProperty("centromere.web.security.secure-url");
        logger.info(String.format("Configuring web security with OPEN READ and OPEN WRITE for "
            + "API root %s", secureUrl));

        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
            .addFilterBefore(authenticationTokenProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            .antMatcher(secureUrl)
            .authorizeRequests()
            .anyRequest().permitAll()
          .and()
            .csrf().disable();

      }
    }

    @Configuration
    @PropertySource(value = { "classpath:web-defaults.properties" })
    @Order(1)
    @Profile(SECURE_READ_WRITE_PROFILE)
    public static class SecuredReadWriteTokenSecurityConfig extends TokenSecurityConfiguration {

      @Autowired private Environment env;

      @Override
      protected void configure(HttpSecurity http) throws Exception {

        String secureUrl = env.getRequiredProperty("centromere.web.security.secure-url");
        logger.info(String.format("Configuring web security with RESTRICTED READ and RESTRICTED "
            + "WRITE for API root: %s", secureUrl));

        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
            .addFilterBefore(authenticationTokenProcessingFilter(),
              UsernamePasswordAuthenticationFilter.class)
            .antMatcher(secureUrl)
            .authorizeRequests()
            .anyRequest().fullyAuthenticated()
          .and()
            .csrf().disable();

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
          .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
            .httpBasic()
          .and()
            .csrf().disable();
      }
    }

  }

}
