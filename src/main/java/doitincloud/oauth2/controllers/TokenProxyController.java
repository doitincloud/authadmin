package doitincloud.oauth2.controllers;

import doitincloud.oauth2.services.TokenProxyServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

@RestController
public class TokenProxyController {

    @Autowired
    private TokenProxyServices tokenProxyServices;

    @RequestMapping(value = "/oauth/v1/{action}", method=RequestMethod.GET)
    public ResponseEntity<Map> get(
            HttpServletRequest request,
            @PathVariable("action") String action,
            @RequestParam Map<String, String> parameters) throws UnsupportedOperationException {
        HttpHeaders headers = prepareHttpHeaders(request);
        return tokenProxyServices.processRequest(action, parameters, headers);
    }

    @RequestMapping(value = "/oauth/v1/{action}", method=RequestMethod.POST)
    public ResponseEntity<Map> post(
            HttpServletRequest request,
            @PathVariable("action") String action,
            @RequestParam Map<String, String> parameters) throws UnsupportedOperationException {
        HttpHeaders headers = prepareHttpHeaders(request);
        return tokenProxyServices.processRequest(action, parameters, headers);
    }

    private HttpHeaders prepareHttpHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> authHeaders = request.getHeaders("Authorization");
        if (authHeaders != null) {
            while (authHeaders.hasMoreElements()) {
                String value = authHeaders.nextElement();
                headers.add("Authorization", value);
            }
        }
        Enumeration<String> cookieHeaders = request.getHeaders("Cookie");
        if (cookieHeaders != null) {
            while (cookieHeaders.hasMoreElements()) {
                String value = cookieHeaders.nextElement();
                headers.add("Cookie", value);
            }
        }
        return headers;
    }
}
