package com.api.framework;

import com.api.framework.helpers.ClientFactory;

import java.util.Map;

public class BaseTest {

    protected Map<String, Object> headers;

    public BaseTest() {
        this.headers = ClientFactory.getHeaders();
    }

    // Common utility methods can go here
}
