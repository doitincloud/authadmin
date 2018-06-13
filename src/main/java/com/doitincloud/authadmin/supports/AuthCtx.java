package com.doitincloud.authadmin.supports;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AuthCtx {

    public static String getAccessToken() {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
        if (details == null) {
            return null;
        }
        return details.getTokenValue();
    }

    public static Set<String> getUserAuthorities() {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Authentication authentication = auth.getUserAuthentication();
        if (authentication == null) {
            return new HashSet<>();
        }
        Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority grantedAuthority: grantedAuthorities) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities;
    }

    public static Set<String> getScope() {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        OAuth2Request request = auth.getOAuth2Request();
        if (request == null) {
            return new HashSet<>();
        }
        Set<String> scope = request.getScope();
        return scope;
    }

    public static Set<String> getResourceIds() {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        OAuth2Request request = auth.getOAuth2Request();
        if (request == null) {
            return null;
        }
        Set<String> resourceIds = request.getResourceIds();
        return resourceIds;
    }

    public static String getUsername() {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (auth.isClientOnly()) {
            return null;
        } else {
            return auth.getName();
        }
    }

    public static String getClientId() {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (auth.isClientOnly()) {
            return auth.getName();
        } else {
            return auth.getOAuth2Request().getClientId();
        }
    }
}
