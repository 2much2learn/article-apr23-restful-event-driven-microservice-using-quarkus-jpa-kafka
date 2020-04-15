package com.toomuch2learn.crud.catalogue.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {

    /**
     * Utility method to convert object to json string
     * @param object
     * @return json
     */
    public static String convertObjectToJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch(JsonProcessingException e) {
            // Ignore
        }
        return null;
    }
}
