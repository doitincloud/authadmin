package com.doitincloud.oauth2.services;

import com.doitincloud.commons.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TokenRevokeServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRevokeServices.class);

    @Value("${oauth2.server_url}")
    private String oauth2ServerUrl;

    private RestTemplate restTemplate = new RestTemplate();

    public boolean revoke(String value, String type) {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
        if (details == null) {
            return false;
        }
        String accessToken = details.getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        formData.add("value", value);
        formData.add("type", type);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        String url = oauth2ServerUrl + "/oauth/v1/revoke";
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                LOGGER.error("failed to revoke token with type: " + type + " value: " + value + " response: "
                        + Utils.toJson(response.getBody()));
                return false;
            }
        } catch (Exception e) {
            throw new ResourceAccessException("failed to revoke token with type: " + type + " value: " + value + " exception message: "
                    + e.getMessage());
        }
        return true;
    }
}
