package com.blueprint.centromere.ws.controller;

import java.util.Arrays;
import java.util.List;

/**
 * Reserved query parameters for API requests.
 * 
 * @author woemler
 */
public final class ReservedRequestParameters {

    public static final String INCLUDED_FIELDS_PARAMETER = "_include";
    public static final String EXCLUDED_FIELDS_PARAMETER = "_exclude";
    public static final List<String> FIELD_FILTER_PARAMETERS
        = Arrays.asList(INCLUDED_FIELDS_PARAMETER, EXCLUDED_FIELDS_PARAMETER);

    public static final String PAGE_PARAMETER = "_page";
    public static final String SIZE_PARAMETER = "_size";
    public static final String SORT_PARAMETER = "_sort";

    public static final List<String> PAGINATION_PARAMETERS
        = Arrays.asList(PAGE_PARAMETER, SIZE_PARAMETER, SORT_PARAMETER);

    public static final String FORMAT_PARAMETER = "_format";
    public static final List<String> OTHER_PARAMETERS = Arrays.asList(FORMAT_PARAMETER);

    private ReservedRequestParameters() {
    }
    
}
