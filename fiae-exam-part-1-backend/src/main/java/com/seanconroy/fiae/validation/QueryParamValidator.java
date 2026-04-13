package com.seanconroy.fiae.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Set;

@ApplicationScoped
public class QueryParamValidator {

    public void validateAllowedParams(MultivaluedMap<String, String> queryParams, Set<String> allowedParams) {
        for (String param : queryParams.keySet()) {
            if (!allowedParams.contains(param)) {
                throw new BadRequestException("Invalid query parameter: " + param);
            }
        }
    }
}
