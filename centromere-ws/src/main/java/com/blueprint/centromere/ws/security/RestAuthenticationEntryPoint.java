package com.blueprint.centromere.ws.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author woemler
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  
  private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response, 
      AuthenticationException exception
  ) throws IOException, ServletException {
    
    logger.error("Returning unauthorized access error: " + exception.getMessage());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
        "You are not authorized to access the requested resource.");
    
  }
}
