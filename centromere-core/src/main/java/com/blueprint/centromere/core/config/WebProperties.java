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

package com.blueprint.centromere.core.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author woemler
 */
@Configuration
@PropertySource("classpath:web-defaults.properties")
@ConfigurationProperties("centromere.web")
public class WebProperties {
  
  private boolean enableStaticContent;
  private String staticContentLocation;
  private String homePage;
  private String homePageLocation;
  private Map<String, String> attributes = new HashMap<>(); 
  
  private Security security = new Security();
  private Api api = new Api();

  public boolean isEnableStaticContent() {
    return enableStaticContent;
  }

  public void setEnableStaticContent(boolean enableStaticContent) {
    this.enableStaticContent = enableStaticContent;
  }

  public String getStaticContentLocation() {
    return staticContentLocation;
  }

  public void setStaticContentLocation(String staticContentLocation) {
    this.staticContentLocation = staticContentLocation;
  }

  public String getHomePage() {
    return homePage;
  }

  public void setHomePage(String homePage) {
    this.homePage = homePage;
  }

  public String getHomePageLocation() {
    return homePageLocation;
  }

  public void setHomePageLocation(String homePageLocation) {
    this.homePageLocation = homePageLocation;
  }

  public Security getSecurity() {
    return security;
  }

  public void setSecurity(Security security) {
    this.security = security;
  }

  public Api getApi() {
    return api;
  }

  public void setApi(Api api) {
    this.api = api;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }
  
  public boolean hasAttribute(String attribute){
    return this.attributes.containsKey(attribute);
  }
  
  public String getAttribute(String attribute){
    return this.attributes.getOrDefault(attribute, null);
  }

  @Override
  public String toString() {
    return "WebProperties{" +
        "enableStaticContent=" + enableStaticContent +
        ", staticContentLocation='" + staticContentLocation + '\'' +
        ", homePage='" + homePage + '\'' +
        ", homePageLocation='" + homePageLocation + '\'' +
        ", attributes='" + attributes + "'" +
        ", security=" + security +
        ", api=" + api +
        '}';
  }

  public static class Security {

    private String token;
    private Long tokenLifespanDays;
    private Long tokenLifespanHours;
    private String secureUrl;
    private boolean secureRead;
    private boolean secureWrite;

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public Long getTokenLifespanDays() {
      return tokenLifespanDays;
    }

    public void setTokenLifespanDays(Long tokenLifespanDays) {
      this.tokenLifespanDays = tokenLifespanDays;
    }

    public Long getTokenLifespanHours() {
      return tokenLifespanHours;
    }

    public void setTokenLifespanHours(Long tokenLifespanHours) {
      this.tokenLifespanHours = tokenLifespanHours;
    }

    public String getSecureUrl() {
      return secureUrl;
    }

    public void setSecureUrl(String secureUrl) {
      this.secureUrl = secureUrl;
    }

    public boolean isSecureRead() {
      return secureRead;
    }

    public void setSecureRead(boolean secureRead) {
      this.secureRead = secureRead;
    }

    public boolean isSecureWrite() {
      return secureWrite;
    }

    public void setSecureWrite(Boolean secureWrite) {
      this.secureWrite = secureWrite;
    }

    @Override
    public String toString() {
      return "Security{" +
          "token='" + token + '\'' +
          ", tokenLifespanDays=" + tokenLifespanDays +
          ", tokenLifespanHours=" + tokenLifespanHours +
          ", secureUrl='" + secureUrl + '\'' +
          ", secureRead=" + secureRead +
          ", secureWrite=" + secureWrite +
          '}';
    }
  }
  
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

    public String getRootUrl() {
      return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
      this.rootUrl = rootUrl;
    }

    public String getRegexUrl() {
      return regexUrl;
    }

    public void setRegexUrl(String regexUrl) {
      this.regexUrl = regexUrl;
    }

    public String getAntMatcherUrl() {
      return antMatcherUrl;
    }

    public void setAntMatcherUrl(String antMatcherUrl) {
      this.antMatcherUrl = antMatcherUrl;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getContactName() {
      return contactName;
    }

    public void setContactName(String contactName) {
      this.contactName = contactName;
    }

    public String getContactUrl() {
      return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
      this.contactUrl = contactUrl;
    }

    public String getContactEmail() {
      return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
      this.contactEmail = contactEmail;
    }

    public String getLicense() {
      return license;
    }

    public void setLicense(String license) {
      this.license = license;
    }

    public String getLicenseUrl() {
      return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
      this.licenseUrl = licenseUrl;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public String getTermsOfService() {
      return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
      this.termsOfService = termsOfService;
    }

    @Override
    public String toString() {
      return "RestApiProperties{" +
          "rootUrl='" + rootUrl + '\'' +
          ", regexUrl='" + regexUrl + '\'' +
          ", antMatcherUrl='" + antMatcherUrl + '\'' +
          ", name='" + name + '\'' +
          ", description='" + description + '\'' +
          ", contactName='" + contactName + '\'' +
          ", contactUrl='" + contactUrl + '\'' +
          ", contactEmail='" + contactEmail + '\'' +
          ", license='" + license + '\'' +
          ", licenseUrl='" + licenseUrl + '\'' +
          ", version='" + version + '\'' +
          ", termsOfService='" + termsOfService + '\'' +
          '}';
    }
    
  }
  
  
}
