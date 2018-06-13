package com.doitincloud.oauth2.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class TokenProxyServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProxyServices.class);

    @Value("${oauth2.server_url}")
    private String oauth2ServerUrl;

    private RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<Map> processRequest(String action, Map<String, String> parameters, HttpHeaders headers)
            throws UnsupportedOperationException {

        if (action.equals("authorize")) {
            throw new UnsupportedOperationException("authorize is not supported");
        }
        if (action.equals("token")) {
            String grantType = parameters.get("grant_type");
            if (grantType == null ||
                (!grantType.equals("password") && !grantType.equals("refresh_token"))) {
                throw new UnsupportedOperationException("token proxy supports grant types: password and refresh_token");
            }
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        for (Map.Entry<String, String> entry: parameters.entrySet()) {
            formData.add(entry.getKey(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        String url = oauth2ServerUrl + "/oauth/v1/" + action;
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return response;
        } catch (Exception e) {
            throw new ResourceAccessException("failed to proxy to: " + url + " exception message: "
                    + e.getMessage());
        }
    }
}
