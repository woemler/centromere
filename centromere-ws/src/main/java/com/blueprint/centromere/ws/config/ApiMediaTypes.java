package com.blueprint.centromere.ws.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;

/**
 * HTTP Mime types for REST web services.
 * 
 * @author woemler
 */
public class ApiMediaTypes {

  public static final String APPLICATION_HAL_JSON_VALUE = "application/hal+json";
  public static final MediaType APPLICATION_HAL_JSON = new MediaType("application", "hal+json");
  public static final String APPLICATION_HAL_XML_VALUE = "application/hal+xml";
  public static final MediaType APPLICATION_HAL_XML = new MediaType("application", "hal+xml");
  public static final String TEXT_PLAIN_UTF8_VALUE = "text/plain; charset=utf-8";
  public static final MediaType TEXT_PLAIN_UTF8 = new MediaType("text", "plain", Charset.forName("utf-8"));

  public static final String[] ACCEPTED_MEDIA_TYPE_VALUES = {
      APPLICATION_HAL_JSON_VALUE, APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE, TEXT_PLAIN_UTF8_VALUE
  };

  public static final MediaType[] ACCEPTED_MEDIA_TYPES = {
      APPLICATION_HAL_JSON, APPLICATION_HAL_XML, MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML, TEXT_PLAIN_UTF8
  };

  public static boolean isHalMediaType(String mediaType){
    return getHalMediaTypeValues().contains(mediaType);
  }

  public static boolean isHalMediaType(MediaType mediaType){
    return getHalMediaTypes().contains(mediaType);
  }

  public static List<String> getHalMediaTypeValues(){
    List<String> mediaTypes = new ArrayList<>();
    mediaTypes.add(APPLICATION_HAL_JSON_VALUE);
    mediaTypes.add(APPLICATION_HAL_XML_VALUE);
    return mediaTypes;
  }

  public static List<MediaType> getHalMediaTypes(){
    List<MediaType> mediaTypes = new ArrayList<>();
    mediaTypes.add(APPLICATION_HAL_JSON);
    mediaTypes.add(APPLICATION_HAL_XML);
    return mediaTypes;
  }

  public static List<MediaType> getJsonMediaTypes(){
    return Arrays.asList(MediaType.APPLICATION_JSON, APPLICATION_HAL_JSON);
  }

  public static List<MediaType> getXmlMediaTypes(){
    return Arrays.asList(MediaType.APPLICATION_XML, APPLICATION_HAL_XML);
  }

  public static List<String> getAllResponseTypeValues(){
    return Arrays.asList(APPLICATION_HAL_JSON_VALUE, APPLICATION_HAL_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, TEXT_PLAIN_UTF8_VALUE);
  }

  public static List<String> getAllAcceptTypeValues(){
    return Arrays.asList(MediaType.APPLICATION_JSON_VALUE);
  }

}
