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

import com.blueprint.centromere.core.repository.DefaultModelRepositoryRegistry;
import com.blueprint.centromere.core.repository.ModelRepositoryRegistry;
import com.blueprint.centromere.ws.controller.ModelCrudController;
import com.blueprint.centromere.ws.controller.ModelResourceAssembler;
import com.blueprint.centromere.ws.controller.ReservedRequestParameters;
import com.blueprint.centromere.ws.controller.UserAuthenticationController;
import com.blueprint.centromere.ws.exception.RestExceptionHandler;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Default web application configurations.
 *
 * @author woemler
 */

public class WebApplicationConfig {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Configuration
    @EnableSpringDataWebSupport
    @ComponentScan(basePackageClasses = {
        UserAuthenticationController.class,
        ModelCrudController.class,
        RestExceptionHandler.class
    })
    @PropertySource(value = { "classpath:web-defaults.properties" })
    public static class DefaultWebApplicationConfig implements WebMvcConfigurer {

        private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationConfig.class);

        @Bean
        public ModelRepositoryRegistry modelRepositoryRegistry(ApplicationContext context) {
            return new DefaultModelRepositoryRegistry(context);
        }

        @Bean
        public ModelResourceRegistry modelResourceRegistry(ApplicationContext context) {
            return new DefaultModelResourceRegistry(context);
        }

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        public ModelResourceAssembler mappingModelResourceAssembler(ModelResourceRegistry modelResourceRegistry) {
            return new ModelResourceAssembler(modelResourceRegistry);
        }

        @Bean
        public HttpMessageConverters customConverters() {

            // JSON
            FilteringJackson2HttpMessageConverter jsonConverter
                = new FilteringJackson2HttpMessageConverter();
            jsonConverter.setSupportedMediaTypes(ApiMediaTypes.getJsonMediaTypes());

            // XML
            MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
            xmlConverter.setSupportedMediaTypes(ApiMediaTypes.getXmlMediaTypes());
            XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
            xmlConverter.setMarshaller(xStreamMarshaller);
            xmlConverter.setUnmarshaller(xStreamMarshaller);

            // Text
            FilteringTextMessageConverter textMessageConverter =
                new FilteringTextMessageConverter(
                    new MediaType("text", "plain", Charset.forName("utf-8")));
            textMessageConverter.setDelimiter("\t");

            return new HttpMessageConverters(jsonConverter, xmlConverter, textMessageConverter);

        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

            // Swagger webjars
            registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
            if (!registry.hasMappingForPattern("/webjars/**")) {
                registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
            } else {
                LOGGER.info("[CENTROMERE] WebJar location already configured.");
            }

        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

            CustomPageableHandlerMethodArgumentResolver resolver
                = new CustomPageableHandlerMethodArgumentResolver();
            resolver.setPageParameterName(ReservedRequestParameters.PAGE_PARAMETER);
            resolver.setSizeParameterName(ReservedRequestParameters.SIZE_PARAMETER);
            resolvers.add(resolver);

        }

    }
    
    private static final class CustomPageableHandlerMethodArgumentResolver 
        implements HandlerMethodArgumentResolver, PageableArgumentResolver {
        
        private String pageParameterName = ReservedRequestParameters.PAGE_PARAMETER;
        private String sizeParameterName = ReservedRequestParameters.SIZE_PARAMETER;
        private String sortParameterName = ReservedRequestParameters.SORT_PARAMETER;
        private Integer defaultPageSize = 10;

        @Nonnull
        @Override
        public Pageable resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer modelAndViewContainer, 
            NativeWebRequest nativeWebRequest,
            WebDataBinderFactory webDataBinderFactory) {

            HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
            Map<String, String[]> paramMap = request.getParameterMap();
            if (!paramMap.containsKey(pageParameterName) 
                && !paramMap.containsKey(sizeParameterName) 
                && !paramMap.containsKey(sortParameterName)) {
                return null;
            }
            
            Integer page = 0;
            Integer size = defaultPageSize;
            Sort sort = null;
            
            if (paramMap.containsKey(sortParameterName) 
                && paramMap.get(sortParameterName).length > 0
                && !"".equals(paramMap.get(sortParameterName)[0].trim())) {
                String sortParam = paramMap.get(sortParameterName)[0].trim();
                if (sortParam.contains(",")) {
                    String[] bits = sortParam.split(",");
                    sort = Sort.by(Direction.fromString(bits[1]), bits[0]);
                } else {
                    sort = Sort.by(Direction.ASC, sortParam);
                }
            } 

            if (paramMap.containsKey(sizeParameterName)
                && paramMap.get(sizeParameterName).length > 0
                && !"".equals(paramMap.get(sizeParameterName)[0].trim())) {
                size = Integer.parseInt(paramMap.get(sizeParameterName)[0]);
            }
            
            if (paramMap.containsKey(pageParameterName)
                && paramMap.get(pageParameterName).length > 0
                && !"".equals(paramMap.get(pageParameterName)[0].trim())) {
                page = Integer.parseInt(paramMap.get(pageParameterName)[0]);
            }
            
            if (sort != null) {
                return PageRequest.of(page, size, sort);
            } else {
                return PageRequest.of(page, size);
            }
            
        }

        @Override
        public boolean supportsParameter(MethodParameter methodParameter) {
            return Pageable.class.isAssignableFrom(methodParameter.getParameterType());
        }

        public void setPageParameterName(String pageParameterName) {
            this.pageParameterName = pageParameterName;
        }

        public void setSizeParameterName(String sizeParameterName) {
            this.sizeParameterName = sizeParameterName;
        }

        public void setSortParameterName(String sortParameterName) {
            this.sortParameterName = sortParameterName;
        }

        public void setDefaultPageSize(Integer defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }
    }

}
