package com.api.framework.helpers;

import java.util.HashMap;
import java.util.Map;

public class ClientFactory {

    public static Map<String, Object> getHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        // Add auth token if needed: headers.put("Authorization", "Bearer ...");
        return headers;
    }
}
