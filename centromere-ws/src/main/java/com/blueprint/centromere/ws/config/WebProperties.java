/*
 * Copyright 2017 the original author or authors
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

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author woemler
 */
@Configuration
@PropertySource("classpath:web-defaults.properties")
@ConfigurationProperties("centromere.web")
@Data
public class WebProperties {
  
  private boolean enableStaticContent;
  private String staticContentLocation;
  private String homePage;
  private String homePageLocation;
  private Map<String, String> attributes = new HashMap<>(); 
  
  private Security security = new Security();
  private Api api = new Api();

  public boolean hasAttribute(String attribute){
    return this.attributes.containsKey(attribute);
  }
  
  public String getAttribute(String attribute){
    return this.attributes.getOrDefault(attribute, null);
  }

  @Data
  public static class Security {

    private String token;
    private Long tokenLifespanDays;
    private Long tokenLifespanHours;
    private String secureUrl;
    private boolean secureRead;
    private boolean secureWrite;
    
  }
  
  @Data
  public static class Api {

    private String rootUrl;
    private String regexUrl;
    private String antMatcherUrl;
    private String name;
    private String description;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String license;
    private String licenseUrl;
    private String version;
    private String termsOfService;
    
  }
  
  
}
